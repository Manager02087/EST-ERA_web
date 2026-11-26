package uz.estera.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "promo_codes")
public class PromoCode {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "master_id", nullable = false) private UUID masterId;
    @Column(nullable = false, unique = true) private String code;
    @Column(name = "discount_type", nullable = false) private String discountType; // PERCENT or FIXED_UZS
    @Column(name = "discount_value", nullable = false) private long discountValue;
    @Column(name = "valid_until", nullable = false) private OffsetDateTime validUntil;
    @Column(name = "max_uses", nullable = false) private int maxUses = 100;
    @Column(name = "used_count", nullable = false) private int usedCount = 0;
    @Column(name = "is_active", nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; }
    public UUID getMasterId() { return masterId; }
    public void setMasterId(UUID masterId) { this.masterId = masterId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }
    public long getDiscountValue() { return discountValue; }
    public void setDiscountValue(long discountValue) { this.discountValue = discountValue; }
    public OffsetDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(OffsetDateTime validUntil) { this.validUntil = validUntil; }
    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }
    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
