#!/usr/bin/env bash
set -euo pipefail

# EST-ERA Daily Automated PostgreSQL Backup (run via cron at 03:00 Tashkent time)
source /opt/est-era/.env
BACKUP_DIR="/opt/est-era/backups"
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
OUT_FILE="${BACKUP_DIR}/estera_db_${TIMESTAMP}.sql.gz"

mkdir -p "${BACKUP_DIR}"
docker exec estera_postgres pg_dump -U "${POSTGRES_USER}" "${POSTGRES_DB}" | gzip > "${OUT_FILE}"

# Keep last 14 days of local backups
find "${BACKUP_DIR}" -type f -name "*.sql.gz" -mtime +14 -delete

SIZE=$(du -h "${OUT_FILE}" | cut -f1)
if [[ -n "${TG_ALERT_BOT_TOKEN:-}" && -n "${TG_ADMIN_CHAT_ID:-}" ]]; then
  curl -sS -X POST "https://api.telegram.org/bot${TG_ALERT_BOT_TOKEN}/sendMessage"     -d chat_id="${TG_ADMIN_CHAT_ID}"     -d text="EST-ERA Backup OK: ${TIMESTAMP} (${SIZE})" >/dev/null
fi
