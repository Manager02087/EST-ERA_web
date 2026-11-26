export type Lang = 'uz' | 'ru' | 'en';

export const DICT: Record<Lang, Record<string, string>> = {
  uz: {
    brandSub: 'VIP 24/7 Xizmatlar Ekotizimi',
    heroTitle: 'Uy, avtomobil va kundalik hayotingiz uchun 20 ta ishonchli xizmat — bir tugmada',
    heroSub: 'Web va Flutter mobil ilova yagona bazada sinxron ishlaydi. 3 bosqichli Escrow (2% / 98%), GPS nazorat va 40 MB gacha isbot hisoboti.',
    navCatalog: 'Xizmatlar',
    navMasters: 'Ustalar',
    navAdmin: 'Admin (9 rol)',
    navAuth: 'Kirish / Ro'yxatdan o'tish',
    orderBtn: 'Buyurtma berish',
  },
  ru: {
    brandSub: 'VIP Экосистема Услуг 24/7',
    heroTitle: '20 проверенных услуг для дома, авто и жизни — в один клик',
    heroSub: 'Веб и мобильное приложение Flutter синхронизированы в реальном времени. 3-этапный Escrow (2% / 98%), GPS-контроль и фото/видео отчёт до 40 МБ.',
    navCatalog: 'Услуги',
    navMasters: 'Мастера',
    navAdmin: 'Админ (9 ролей)',
    navAuth: 'Вход / Регистрация',
    orderBtn: 'Заказать',
  },
  en: {
    brandSub: '24/7 VIP Service Ecosystem',
    heroTitle: '20 verified home, auto & lifestyle services — one tap away',
    heroSub: 'Real-time sync between Web and Flutter App. 3-stage Escrow (2% / 98%), GPS arrival verification, and up to 40 MB proof uploads.',
    navCatalog: 'Services',
    navMasters: 'Masters',
    navAdmin: 'Admin (9 Roles)',
    navAuth: 'Sign In / Register',
    orderBtn: 'Book Now',
  },
};
