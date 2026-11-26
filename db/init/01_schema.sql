-- EST-ERA Unified 24/7 PostgreSQL 16 Schema (est_era_db: Web + Flutter Mobile App)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Exact 9-Role RBAC Hierarchy (EST-ERA Modules Spec)
CREATE TYPE user_role AS ENUM (
    'CLIENT',
    'MASTER',
    'SUPER_ADMIN',
    'ADMIN_S1',
    'ADMIN_S2',
    'ADMIN_S3',
    'SUPPORT_L1',
    'SUPPORT_L2',
    'SUPPORT_L3'
);

CREATE TYPE order_status AS ENUM (
    'CREATED',
    'NEGOTIATING',
    'AWAITING_PAYMENT',
    'ESCROW_HELD',
    'MASTER_EN_ROUTE',
    'ARRIVED_GPS_VERIFIED',
    'IN_PROGRESS',
    'PROOF_UPLOADED',
    'COMPLETED',
    'DISPUTED',
    'CANCELLED',
    'REFUNDED'
);

CREATE TYPE payment_provider AS ENUM ('CLICK', 'PAYME', 'UZUM', 'WALLET', 'TEST_MODE');

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone           VARCHAR(20) UNIQUE NOT NULL,
    email           VARCHAR(120) UNIQUE,
    full_name       VARCHAR(150) NOT NULL,
    passport_masked VARCHAR(32),
    password_hash   TEXT NOT NULL,
    role            user_role NOT NULL DEFAULT 'CLIENT',
    preferred_lang  VARCHAR(5) NOT NULL DEFAULT 'uz' CHECK (preferred_lang IN ('uz', 'ru', 'en')),
    avatar_url      TEXT,
    wallet_uzs      BIGINT NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    is_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE otp_codes (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone           VARCHAR(20) NOT NULL,
    code_hash       TEXT NOT NULL,
    expires_at      TIMESTAMPTZ NOT NULL,
    attempts        SMALLINT NOT NULL DEFAULT 0,
    consumed        BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_otp_phone_active ON otp_codes(phone, consumed, expires_at);

CREATE TABLE service_categories (
    id              SERIAL PRIMARY KEY,
    slug            VARCHAR(80) UNIQUE NOT NULL,
    name_uz         VARCHAR(150) NOT NULL,
    name_ru         VARCHAR(150) NOT NULL,
    name_en         VARCHAR(150) NOT NULL,
    desc_uz         TEXT NOT NULL,
    desc_ru         TEXT NOT NULL,
    desc_en         TEXT NOT NULL,
    group_key       VARCHAR(30) NOT NULL DEFAULT 'home',
    icon_key        VARCHAR(50) NOT NULL,
    base_price_uzs  BIGINT NOT NULL DEFAULT 150000,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order      SMALLINT NOT NULL DEFAULT 0
);

CREATE TABLE master_profiles (
    user_id             UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    bio_uz              TEXT,
    bio_ru              TEXT,
    bio_en              TEXT,
    experience_years    SMALLINT NOT NULL DEFAULT 1,
    tashkent_district   VARCHAR(80) NOT NULL DEFAULT 'Chilonzor',
    rating_avg          NUMERIC(3,2) NOT NULL DEFAULT 5.00,
    completed_orders    INTEGER NOT NULL DEFAULT 0,
    hourly_rate_uzs     BIGINT NOT NULL DEFAULT 150000,
    kyc_status          VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (kyc_status IN ('PENDING', 'VERIFIED', 'REJECTED')),
    is_online_24x7      BOOLEAN NOT NULL DEFAULT FALSE,
    last_lat            DOUBLE PRECISION,
    last_lng            DOUBLE PRECISION,
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE master_services (
    master_id           UUID REFERENCES master_profiles(user_id) ON DELETE CASCADE,
    service_id          INTEGER REFERENCES service_categories(id) ON DELETE CASCADE,
    custom_price_uzs    BIGINT,
    PRIMARY KEY (master_id, service_id)
);

CREATE TABLE saved_masters (
    client_id           UUID REFERENCES users(id) ON DELETE CASCADE,
    master_id           UUID REFERENCES users(id) ON DELETE CASCADE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (client_id, master_id)
);

-- Master-created Promo Codes (Skidka: % or fixed UZS with expiry)
CREATE TABLE promo_codes (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    master_id           UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    code                VARCHAR(32) UNIQUE NOT NULL,
    discount_type       VARCHAR(16) NOT NULL CHECK (discount_type IN ('PERCENT', 'FIXED_UZS')),
    discount_value      BIGINT NOT NULL CHECK (discount_value > 0),
    valid_until         TIMESTAMPTZ NOT NULL,
    max_uses            INTEGER NOT NULL DEFAULT 100,
    used_count          INTEGER NOT NULL DEFAULT 0,
    is_active           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE orders (
    id                          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_code                  VARCHAR(24) UNIQUE NOT NULL,
    client_id                   UUID NOT NULL REFERENCES users(id),
    master_id                   UUID REFERENCES users(id),
    service_id                  INTEGER NOT NULL REFERENCES service_categories(id),
    promo_code_id               UUID REFERENCES promo_codes(id),
    status                      order_status NOT NULL DEFAULT 'CREATED',
    tashkent_district           VARCHAR(80) NOT NULL,
    address_text                TEXT NOT NULL,
    client_lat                  DOUBLE PRECISION,
    client_lng                  DOUBLE PRECISION,
    master_start_lat            DOUBLE PRECISION,
    master_start_lng            DOUBLE PRECISION,
    master_current_lat          DOUBLE PRECISION,
    master_current_lng          DOUBLE PRECISION,
    scheduled_at                TIMESTAMPTZ,
    base_amount_uzs             BIGINT NOT NULL CHECK (base_amount_uzs >= 0),
    discount_uzs                BIGINT NOT NULL DEFAULT 0,
    total_amount_uzs            BIGINT NOT NULL CHECK (total_amount_uzs >= 0),
    platform_fee_uzs            BIGINT NOT NULL DEFAULT 0,
    master_payout_uzs           BIGINT NOT NULL DEFAULT 0,
    payment_method              payment_provider NOT NULL DEFAULT 'TEST_MODE',
    support_chat_access_granted BOOLEAN NOT NULL DEFAULT FALSE,
    client_confirmed_at         TIMESTAMPTZ,
    master_confirmed_at         TIMESTAMPTZ,
    notes                       TEXT,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_orders_client ON orders(client_id, created_at DESC);
CREATE INDEX idx_orders_master ON orders(master_id, status);
CREATE INDEX idx_orders_status ON orders(status);

CREATE TABLE escrow_ledger (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id            UUID NOT NULL REFERENCES orders(id),
    entry_type          VARCHAR(40) NOT NULL CHECK (entry_type IN ('HOLD', 'PLATFORM_FEE_2PCT', 'MASTER_RELEASE_98PCT', 'CLIENT_REFUND', 'MASTER_GPS_COMPENSATION')),
    amount_uzs          BIGINT NOT NULL,
    provider            payment_provider NOT NULL,
    external_tx_id      VARCHAR(120),
    created_by          UUID REFERENCES users(id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Order Proofs: strictly <= 40 MB (41,943,040 bytes) total per order (Photo + Video + report.txt)
CREATE TABLE order_proofs (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id            UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    uploaded_by         UUID NOT NULL REFERENCES users(id),
    object_key          TEXT NOT NULL,
    original_filename   VARCHAR(255) NOT NULL,
    mime_type           VARCHAR(100) NOT NULL,
    file_size_bytes     BIGINT NOT NULL CHECK (file_size_bytes > 0 AND file_size_bytes <= 41943040),
    proof_type          VARCHAR(20) NOT NULL CHECK (proof_type IN ('PHOTO', 'VIDEO', 'REPORT_TXT')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE chat_messages (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id            UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    sender_id           UUID NOT NULL REFERENCES users(id),
    body                TEXT NOT NULL,
    is_read             BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_chat_order ON chat_messages(order_id, created_at ASC);

CREATE TABLE reviews (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id            UUID NOT NULL REFERENCES orders(id),
    reviewer_id         UUID NOT NULL REFERENCES users(id),
    target_user_id      UUID NOT NULL REFERENCES users(id),
    rating              SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment             TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (order_id, reviewer_id)
);

CREATE TABLE disputes (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id            UUID UNIQUE NOT NULL REFERENCES orders(id),
    opened_by           UUID NOT NULL REFERENCES users(id),
    assigned_staff_id   UUID REFERENCES users(id),
    escalation_tier     VARCHAR(20) NOT NULL DEFAULT 'SUPPORT_L2' CHECK (escalation_tier IN ('SUPPORT_L2', 'SUPPORT_L3', 'ADMIN_S3')),
    reason              TEXT NOT NULL,
    status              VARCHAR(25) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'INVESTIGATING', 'RESOLVED_CLIENT', 'RESOLVED_MASTER', 'SPLIT_GPS')),
    resolution_notes    TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    resolved_at         TIMESTAMPTZ
);

CREATE TABLE site_settings (
    key                 VARCHAR(80) PRIMARY KEY,
    value_uz            TEXT NOT NULL,
    value_ru            TEXT NOT NULL,
    value_en            TEXT NOT NULL,
    updated_by          UUID REFERENCES users(id),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE audit_logs (
    id                  BIGSERIAL PRIMARY KEY,
    actor_id            UUID REFERENCES users(id),
    actor_role          user_role,
    action              VARCHAR(100) NOT NULL,
    target_entity       VARCHAR(80),
    target_id           VARCHAR(80),
    ip_address          VARCHAR(64),
    details             JSONB,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
