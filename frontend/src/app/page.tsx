'use client';

import { useEffect, useState } from 'react';
import { getPublicServices, ServiceItem } from '@/lib/api';
import { DICT, Lang } from '@/lib/i18n';

export default function HomePage() {
  const [lang, setLang] = useState<Lang>('uz');
  const [services, setServices] = useState<ServiceItem[]>([]);
  const [query, setQuery] = useState('');

  useEffect(() => {
    getPublicServices().then(setServices).catch(() => setServices([]));
  }, []);

  const t = DICT[lang];
  const filtered = services.filter((s) =>
    `${s.nameUz} ${s.nameRu} ${s.nameEn} ${s.descUz}`.toLowerCase().includes(query.toLowerCase())
  );

  return (
    <div className="space-y-8">
      <section className="rounded-3xl border border-gold/25 bg-surface p-8 space-y-4">
        <div className="flex items-center justify-between flex-wrap gap-3">
          <span className="text-xs font-semibold px-3 py-1 rounded-full bg-gold/10 text-gold border border-gold/30">
            {t.brandSub}
          </span>
          <div className="flex gap-1 bg-obsidian p-1 rounded-xl border border-white/10">
            {(['uz', 'ru', 'en'] as Lang[]).map((l) => (
              <button
                key={l}
                onClick={() => setLang(l)}
                className={`px-2.5 py-1 rounded-lg text-xs font-bold uppercase ${
                  lang === l ? 'gold-gradient text-obsidian' : 'text-gray-400'
                }`}
              >
                {l}
              </button>
            ))}
          </div>
        </div>
        <h1 className="text-3xl font-extrabold text-white max-w-3xl">{t.heroTitle}</h1>
        <p className="text-sm text-gray-300 max-w-2xl">{t.heroSub}</p>
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="20 ta xizmatdan qidiring..."
          className="w-full max-w-lg bg-obsidian border border-white/15 rounded-xl px-4 py-2.5 text-sm text-white focus:border-gold focus:outline-none"
        />
      </section>

      <section className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {filtered.map((s) => (
          <div key={s.id} className="rounded-2xl bg-surface border border-gold/15 hover:border-gold/50 p-5 flex flex-col justify-between gap-4 transition">
            <div>
              <h2 className="font-bold text-white text-sm">
                {lang === 'uz' ? s.nameUz : lang === 'ru' ? s.nameRu : s.nameEn}
              </h2>
              <p className="text-xs text-gray-400 mt-1">
                {lang === 'uz' ? s.descUz : lang === 'ru' ? s.descRu : s.descEn}
              </p>
            </div>
            <div className="pt-3 border-t border-white/10 flex items-center justify-between">
              <span className="text-xs font-extrabold text-gold">
                {s.basePriceUzs.toLocaleString('ru-RU')} so&apos;m
              </span>
              <a href="/auth" className="px-3 py-1.5 rounded-xl bg-gold/15 text-gold hover:bg-gold hover:text-obsidian text-xs font-bold transition">
                {t.orderBtn}
              </a>
            </div>
          </div>
        ))}
      </section>
    </div>
  );
}
