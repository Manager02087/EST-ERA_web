'use client';

import { FormEvent, useEffect, useState } from 'react';
import { createPromo, getPublicMasters, MasterItem } from '@/lib/api';

const DISTRICTS = ['all', 'Chilonzor', 'Yunusobod', "Mirzo Ulug'bek", 'Yakkasaroy', 'Mirobod', 'Shayxontohur', 'Olmazor', 'Uchtepa', 'Sergeli', 'Yashnobod', 'Bektemir', 'Yangihayot'];

export default function MastersPage() {
  const [masters, setMasters] = useState<MasterItem[]>([]);
  const [district, setDistrict] = useState('all');
  const [onlineOnly, setOnlineOnly] = useState(false);
  const [promoCode, setPromoCode] = useState('');
  const [discountType, setDiscountType] = useState<'PERCENT' | 'FIXED_UZS'>('PERCENT');
  const [discountValue, setDiscountValue] = useState(10);
  const [validHours, setValidHours] = useState(48);
  const [promoMsg, setPromoMsg] = useState('');

  useEffect(() => {
    getPublicMasters(district, onlineOnly).then(setMasters).catch(() => setMasters([]));
  }, [district, onlineOnly]);

  async function onCreatePromo(e: FormEvent) {
    e.preventDefault();
    try {
      await createPromo({ code: promoCode, discountType, discountValue, validHours, maxUses: 50 });
      setPromoMsg(`Promo-kod "${promoCode.toUpperCase()}" yaratildi!`);
      setPromoCode('');
    } catch (err) {
      setPromoMsg(`Xatolik (MASTER roli bilan kiring): ${(err as Error).message}`);
    }
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-white">Tekshirilgan ustalar katalogi (HeadHunter uslubi)</h1>
          <p className="text-sm text-gray-400 mt-1">Reyting, KYC holati, Toshkent tumanlari va 24/7 onlayn statusi bo&apos;yicha saralang.</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <select value={district} onChange={(e) => setDistrict(e.target.value)} className="bg-surface border border-white/15 rounded-xl px-3 py-2 text-xs text-white">
            {DISTRICTS.map((d) => <option key={d} value={d}>{d === 'all' ? 'Barcha tumanlar (12)' : d}</option>)}
          </select>
          <button onClick={() => setOnlineOnly(!onlineOnly)} className={`px-3 py-2 rounded-xl text-xs font-semibold border ${onlineOnly ? 'border-emerald-400 text-emerald-400 bg-emerald-500/10' : 'border-white/15 text-gray-300 bg-surface'}`}>
            Faqat 24/7 onlayn
          </button>
        </div>
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {masters.length === 0 ? (
          <div className="col-span-full bg-surface border border-white/10 rounded-2xl p-6 text-sm text-gray-400">
            Hozircha bu filtr bo&apos;yicha ustalar topilmadi (backend ishga tushgach PostgreSQL bazasidan yuklanadi).
          </div>
        ) : masters.map((m) => (
          <div key={m.userId} className="bg-surface border border-gold/20 rounded-2xl p-5 space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-gold">★ {Number(m.ratingAvg).toFixed(2)} · {m.kycStatus}</span>
              <span className={`text-[11px] px-2 py-0.5 rounded-full font-semibold ${m.online24x7 ? 'bg-emerald-500/15 text-emerald-400' : 'bg-gray-800 text-gray-400'}`}>
                {m.online24x7 ? '24/7 Online' : 'Offline'}
              </span>
            </div>
            <div className="text-sm font-bold text-white">{m.tashkentDistrict} · {m.experienceYears} yil tajriba</div>
            <p className="text-xs text-gray-400">{m.bioUz || 'Professional mutaxassis'}</p>
            <div className="pt-2 border-t border-white/10 flex items-center justify-between text-xs">
              <span className="font-bold text-gold">{m.hourlyRateUzs.toLocaleString('ru-RU')} so&apos;m / soat</span>
              <span className="text-gray-400">{m.completedOrders} buyurtma</span>
            </div>
          </div>
        ))}
      </div>

      {/* Master Promo-Code Creator (EST-ERA Modules Spec #2) */}
      <section className="bg-surface border border-gold/25 rounded-3xl p-6 space-y-4">
        <h2 className="text-lg font-extrabold text-gold">Ustalar uchun Skidka / Promo-kod yaratish</h2>
        <form onSubmit={onCreatePromo} className="grid sm:grid-cols-2 lg:grid-cols-5 gap-3 text-xs">
          <input required value={promoCode} onChange={(e) => setPromoCode(e.target.value)} placeholder="Kod (masalan: VIP20)" className="bg-obsidian border border-white/15 rounded-xl px-3 py-2.5 text-white uppercase" />
          <select value={discountType} onChange={(e) => setDiscountType(e.target.value as 'PERCENT' | 'FIXED_UZS')} className="bg-obsidian border border-white/15 rounded-xl px-3 py-2.5 text-white">
            <option value="PERCENT">Foiz (%)</option>
            <option value="FIXED_UZS">Aniq summa (so&apos;m)</option>
          </select>
          <input type="number" min={1} value={discountValue} onChange={(e) => setDiscountValue(Number(e.target.value))} placeholder="Qiymat" className="bg-obsidian border border-white/15 rounded-xl px-3 py-2.5 text-white" />
          <input type="number" min={1} value={validHours} onChange={(e) => setValidHours(Number(e.target.value))} placeholder="Amal qilish (soat)" className="bg-obsidian border border-white/15 rounded-xl px-3 py-2.5 text-white" />
          <button type="submit" className="gold-gradient text-obsidian font-bold rounded-xl py-2.5">Promo yaratish</button>
        </form>
        {promoMsg && <p className="text-xs text-gold">{promoMsg}</p>}
      </section>
    </div>
  );
}
