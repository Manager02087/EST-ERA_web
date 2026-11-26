# EST-ERA 24/7 Web Ekotizimi — Ishga Tushirish Qo'llanmasi (26-noyabr -> 7-dekabr 2026)

## 1. Hamyonbop 24/7 Server Tavsiyasi (1000+ foydalanuvchi va masshtablanish uchun)
- **VPS:** 4 vCPU, 8 GB RAM, 80 GB NVMe SSD (Ubuntu 24.04 LTS) — masalan, Hetzner Cloud CPX31 (~$15/oy) yoki O'zbekistondagi Tas-IX VPS provayderi.
- **DDoS va DNS:** Cloudflare Free plan (bepul DDoS himoya, CDN, SSL).
- **Domen:** `est-era.uz` (aHost / Arsenal-D orqali ro'yxatdan o'tkazilib, DNS Cloudflare'ga ulanadi).

## 2. Serverda 24/7 ishga tushirish qadamlari
```bash
# 1. Serverga Docker o'rnatish
curl -fsSL https://get.docker.com | sh

# 2. Loyihani /opt/est-era ga joylash va .env sozlash
cp .env.example .env
nano .env

# 3. Barcha servislarni 24/7 rejimda ishga tushirish
docker compose -f docker-compose.prod.yml up -d --build

# 4. Kunlik backup va 2 daqiqalik Telegram monitoringni cron'ga qo'shish
chmod +x ops/backup.sh ops/health_monitor.sh
(crontab -l 2>/dev/null; echo "0 3 * * * /opt/est-era/ops/backup.sh") | crontab -
(crontab -l 2>/dev/null; echo "*/2 * * * * /opt/est-era/ops/health_monitor.sh") | crontab -
```

## 3. 7-dekabrgacha 11 kunlik ish rejasi
- **26–28 noyabr:** PostgreSQL baza, Spring Boot Auth (JWT + OTP), 9-rol RBAC va 20 ta xizmat API.
- **29 noyabr – 2 dekabr:** Next.js VIP Dark Luxury asosiy sayt (UZ/RU/EN), buyurtma + 3 bosqichli Escrow (test rejim), HeadHunter uslubidagi usta profillari.
- **3–5 dekabr:** 9-rolli Web Admin Panel (Operator, Moderator, Moliya, Arbitraj, Kontent, Analitik) + STOMP real-time chat.
- **6–7 dekabr:** VPS + Cloudflare + SSL + GitHub Actions CI/CD + Flutter ilova bilan yakuniy sinov va 24/7 ishga tushirish.
