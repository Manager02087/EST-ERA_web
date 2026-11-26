INSERT INTO service_categories (slug, name_uz, name_ru, name_en, desc_uz, desc_ru, desc_en, group_key, icon_key, base_price_uzs, sort_order) VALUES
('home-cleaning', 'Uy tozalash', 'Уборка дома', 'Home Cleaning', 'Umumiy va muntazam tozalash', 'Генеральная и регулярная', 'Deep and regular cleaning', 'home', 'sparkles', 250000, 1),
('plumbing', 'Santexnika', 'Сантехника', 'Plumbing', 'Quvurlar va kranlarni ta''mirlash', 'Ремонт труб и кранов', 'Pipe and faucet repair', 'repair', 'wrench', 150000, 2),
('painting', 'Bo''yoqchilik ishlari', 'Малярные работы', 'Painting Works', 'Devor va shiftlarni bo''yash', 'Покраска стен и потолков', 'Wall and ceiling painting', 'repair', 'paint-roller', 300000, 3),
('electrical', 'Elektrika', 'Электрика', 'Electrical', 'Rozetkalar va yoritish tizimlari', 'Розетки и освещение', 'Outlets and lighting systems', 'repair', 'zap', 150000, 4),
('furniture-dry-cleaning', 'Mebel kimyoviy tozalash', 'Химчистка мебели', 'Furniture Dry Cleaning', 'Divan va kreslolarni chuqur tozalash', 'Диваны и кресла', 'Sofas and armchairs deep clean', 'home', 'armchair', 280000, 5),
('clothes-dry-cleaning', 'Kiyim kimyoviy tozalash', 'Химчистка одежды', 'Clothes Dry Cleaning', 'Kiyim-kechak va tekstil parvarishi', 'Одежда и текстиль', 'Garments and textiles care', 'home', 'shirt', 120000, 6),
('mobile-car-wash', 'Sayyor avtoyuvish', 'Мойка автомобиля', 'Mobile Car Wash', 'Mijoz manzilida joyida yuvish', 'На месте у клиента', 'On-site at client location', 'auto', 'car', 140000, 7),
('mobile-fuel-delivery', 'Avtomobilga yonilg''i quyish', 'Заправка автомобиля', 'Mobile Fuel Delivery', 'Litsenziyali hamkor orqali yonilg''i yetkazish', 'Заказ топлива с доставкой через легального партнёра', 'Fuel delivery via licensed partner', 'auto', 'fuel', 180000, 8),
('handyman', 'Soatlik usta', 'Муж на час', 'Handyman', 'Mayda maishiy ta''mirlash ishlari', 'Мелкий бытовой ремонт', 'Minor household repairs', 'repair', 'hammer', 120000, 9),
('ac-service', 'Konditsionerlar', 'Кондиционеры', 'AC Service', 'Tozalash, freon va texnik xizmat', 'Чистка и обслуживание', 'Cleaning and maintenance', 'repair', 'snowflake', 200000, 10),
('appliance-repair', 'Texnika ta''mirlash', 'Ремонт техники', 'Appliance Repair', 'Muzlatkich va kir yuvish mashinalari', 'Холодильники и стиральные машины', 'Refrigerators and washing machines', 'repair', 'cpu', 180000, 11),
('moving', 'Ko''chish xizmati', 'Переезд', 'Moving Service', 'Buyum va mebellarni xavfsiz tashish', 'Перевозка вещей и мебели', 'Safe transport of goods and furniture', 'special', 'truck', 450000, 12),
('pest-control', 'Dezinseksiya', 'Дезинсекция', 'Pest Control', 'Mutaxassislar tomonidan hasharotlarga qarshi ishlov', 'Обработка от насекомых специалистами', 'Professional insect treatment', 'home', 'shield-check', 250000, 13),
('gardening', 'Bog'' parvarishi', 'Уход за садом', 'Gardening', 'Gazon va o''simliklarni parvarishlash', 'Газон и растения', 'Lawn and plant care', 'home', 'trees', 220000, 14),
('window-cleaning', 'Oyna yuvish', 'Мытьё окон', 'Window Cleaning', 'Deraza va balkonlarni yaltiratib yuvish', 'Окна и балконы', 'Windows and balconies cleaning', 'home', 'wind', 160000, 15),
('home-chef', 'Uy oshpazi', 'Домашний повар', 'Home Chef', 'Uyda mazali taomlar tayyorlash', 'Приготовление еды', 'Private in-home cooking', 'special', 'chef-hat', 350000, 16),
('furniture-assembly', 'Mebel yig''ish', 'Сборка мебели', 'Furniture Assembly', 'Shkaf, stol va karavotlarni yig''ish', 'Шкафы, столы и кровати', 'Wardrobes, tables, and beds', 'repair', 'box', 180000, 17),
('locksmith', 'Chilangar (Qulf ustasi)', 'Слесарь', 'Locksmith', 'Qulflar va eshiklarni ochish/ta''mirlash', 'Замки и двери', 'Locks and doors service', 'repair', 'key-round', 150000, 18),
('roadside-assistance', 'Yo''lda yordam', 'Помощь на дороге', 'Roadside Assistance', 'Akkumulyator quvvatlash va nosozlikda yordam', 'Запуск аккумулятора и помощь при поломке', 'Jump-start and breakdown help', 'auto', 'car-front', 200000, 19),
('pet-care', 'Uy hayvonlari parvarishi', 'Уход за животными', 'Pet Care', 'Sayr qildirish va g''amxo''rlik qilish', 'Выгул и уход за питомцами', 'Walking and pet sitting', 'special', 'paw-print', 130000, 20)
ON CONFLICT (slug) DO NOTHING;

INSERT INTO site_settings (key, value_uz, value_ru, value_en) VALUES
('support_phone', '+998 71 000 00 00 (Vaqtinchalik)', '+998 71 000 00 00 (Временный)', '+998 71 000 00 00 (Temporary)'),
('telegram_url', 'https://t.me/estera_uz_temp', 'https://t.me/estera_uz_temp', 'https://t.me/estera_uz_temp'),
('instagram_url', 'https://instagram.com/estera.uz.temp', 'https://instagram.com/estera.uz.temp', 'https://instagram.com/estera.uz.temp'),
('support_email', 'support@est-era.uz', 'support@est-era.uz', 'support@est-era.uz'),
('office_address', 'Toshkent sh., Yashnobod tumani (Vaqtinchalik)', 'г. Ташкент, Яшнабадский р-н (Временно)', 'Tashkent, Yashnobod district (Temporary)')
ON CONFLICT (key) DO NOTHING;
