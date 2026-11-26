#!/usr/bin/env bash
set -euo pipefail

# EST-ERA 24/7 Uptime & Disk Monitor (run every 2 minutes via cron)
source /opt/est-era/.env

send_alert() {
  local msg="$1"
  if [[ -n "${TG_ALERT_BOT_TOKEN:-}" && -n "${TG_ADMIN_CHAT_ID:-}" ]]; then
    curl -sS -X POST "https://api.telegram.org/bot${TG_ALERT_BOT_TOKEN}/sendMessage"       -d chat_id="${TG_ADMIN_CHAT_ID}"       -d text="ALERT [EST-ERA 24/7]: ${msg}" >/dev/null || true
  fi
}

if ! docker exec estera_backend wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then
  send_alert "Backend API is DOWN! Attempting auto-restart..."
  docker compose -f /opt/est-era/docker-compose.prod.yml restart backend
fi

if ! docker exec estera_frontend wget -qO- http://localhost:3000/ >/dev/null 2>&1; then
  send_alert "Frontend Web is DOWN! Attempting auto-restart..."
  docker compose -f /opt/est-era/docker-compose.prod.yml restart frontend
fi

DISK_USED=$(df / | awk 'NR==2 {gsub("%",""); print $5}')
if (( DISK_USED > 85 )); then
  send_alert "Server disk usage critical: ${DISK_USED}%!"
fi
