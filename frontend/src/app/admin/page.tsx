'use client';

import { FormEvent, useEffect, useState } from 'react';
import {
  DisputeItem, escalateDispute, getAdminDashboard, getAdminDisputes,
  getAdminOrders, getPublicMasters, MasterItem, OrderItem,
  resolveDispute, reviewMasterKyc, updateSiteSetting,
} from '@/lib/api';

export default function AdminPage() {
  const [tab, setTab] = useState<'orders' | 'disputes' | 'kyc' | 'settings'>('orders');
  const [kpi, setKpi] = useState({ totalUsers: 0, totalOrders: 0, totalEscrowVolumeUzs: 0, platformCommission2PctUzs: 0, openDisputes: 0 });
  const [orders, setOrders] = useState<OrderItem[]>([]);
  const [disputes, setDisputes] = useState<DisputeItem[]>([]);
  const [masters, setMasters] = useState<MasterItem[]>([]);
  const [phone, setPhone] = useState('+998 71 000 00 00');
  const [telegram, setTelegram] = useState('https://t.me/estera_uz_temp');
  const [notice, setNotice] = useState('');

  const refresh = () => {
    getAdminDashboard().then(setKpi).catch(() => {});
    getAdminOrders().then(setOrders).catch(() => {});
    getAdminDisputes().then(setDisputes).catch(() => {});
    getPublicMasters().then(setMasters).catch(() => {});
  };

  useEffect(() => { refresh(); }, []);

  async function onSaveContacts(e: FormEvent) {
    e.preventDefault();
    try {
      await updateSiteSetting('support_phone', phone, phone, phone);
      await updateSiteSetting('telegram_url', telegram, telegram, telegram);
      setNotice('Aloqa ma'lumotlari bazada yangilandi!');
    } catch (err) {
      setNotice(`Huquq yetarli emas (SUPER_ADMIN / S1 / S3): ${(err as Error).message}`);
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <div className="text-xs uppercase tracking-wider text-gold font-semibold">EST-ERA 9-Rolli RBAC Boshqaruv</div>
          <h1 className="text-2xl font-extrabold text-white mt-0.5">Super Admin · S1–S3 · Support L1–L3</h1>
        </div>
        {notice && <span className="text-xs px-3 py-1.5 rounded-xl bg-gold/15 text-gold font-semibold">{notice}</span>}
      </div>

      {/* Live KPIs */}
      <div className="grid grid-cols-2 lg:grid-cols-5 gap-3 text-xs">
        <div className="bg-surface border border-white/10 rounded-2xl p-4"><div className="text-gray-400">Foydalanuvchilar</div><div className="text-xl font-extrabold text-white mt-1">{kpi.totalUsers}</div></div>
        <div className="bg-surface border border-white/10 rounded-2xl p-4"><div className="text-gray-400">Buyurtmalar</div><div className="text-xl font-extrabold text-white mt-1">{kpi.totalOrders}</div></div>
        <div className="bg-surface border border-white/10 rounded-2xl p-4"><div className="text-gray-400">Escrow aylanmasi</div><div className="text-lg font-extrabold text-gold mt-1">{kpi.totalEscrowVolumeUzs.toLocaleString('ru-RU')}</div></div>
        <div className="bg-surface border border-white/10 rounded-2xl p-4"><div className="text-gray-400">2% Komissiya</div><div className="text-lg font-extrabold text-emerald-400 mt-1">{kpi.platformCommission2PctUzs.toLocaleString('ru-RU')}</div></div>
        <div className="bg-surface border border-white/10 rounded-2xl p-4"><div className="text-gray-400">Ochiq nizolar</div><div className="text-xl font-extrabold text-rose-400 mt-1">{kpi.openDisputes}</div></div>
      </div>

      {/* Tabs */}
      <div className="flex flex-wrap gap-2 text-xs font-bold">
        {(['orders', 'disputes', 'kyc', 'settings'] as const).map((t) => (
          <button key={t} onClick={() => setTab(t)} className={`px-4 py-2 rounded-xl border ${tab === t ? 'bg-gold/15 border-gold text-gold' : 'bg-surface border-white/10 text-gray-300'}`}>
            {t === 'orders' ? 'Buyurtmalar & Escrow' : t === 'disputes' ? 'Nizolar & Arbitraj' : t === 'kyc' ? 'Ustalar KYC' : 'Aloqa Sozlamalari'}
          </button>
        ))}
      </div>

      {tab === 'orders' && (
        <div className="bg-surface border border-white/10 rounded-2xl p-5 overflow-x-auto text-xs">
          <table className="w-full text-left min-w-[600px]">
            <thead className="text-gray-400 border-b border-white/10">
              <tr><th className="py-2">Kod</th><th>Tuman</th><th>Summa</th><th>2% / 98%</th><th>Holat</th></tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {orders.map((o) => (
                <tr key={o.id}>
                  <td className="py-2.5 font-mono text-gold font-bold">{o.orderCode}</td>
                  <td>{o.tashkentDistrict}</td>
                  <td>{o.totalAmountUzs.toLocaleString('ru-RU')}</td>
                  <td>{o.platformFeeUzs.toLocaleString('ru-RU')} / {o.masterPayoutUzs.toLocaleString('ru-RU')}</td>
                  <td><span className="px-2 py-0.5 rounded bg-gold/15 text-gold">{o.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {tab === 'disputes' && (
        <div className="space-y-3 text-xs">
          {disputes.length === 0 ? <div className="bg-surface border border-white/10 rounded-2xl p-5 text-gray-400">Ochiq nizolar yo&apos;q.</div> : disputes.map((d) => (
            <div key={d.id} className="bg-surface border border-gold/20 rounded-2xl p-4 flex flex-wrap items-center justify-between gap-3">
              <div>
                <div className="font-bold text-white">{d.escalationTier} · {d.status}</div>
                <div className="text-gray-400 mt-0.5">{d.reason}</div>
              </div>
              <div className="flex gap-2">
                <button onClick={async () => { await escalateDispute(d.id); setNotice('Yuqori pog'onaga oshirildi'); refresh(); }} className="px-3 py-1.5 rounded-xl bg-amber-500/20 text-amber-300 font-bold">Escalate (L2→L3→S3)</button>
                <button onClick={async () => { await resolveDispute(d.id, 'RESOLVED_CLIENT', 'Mijoz foydasiga hal qilindi'); setNotice('Nizo yopildi'); refresh(); }} className="px-3 py-1.5 rounded-xl bg-emerald-500/20 text-emerald-300 font-bold">Mijozga qaytarish</button>
              </div>
            </div>
          ))}
        </div>
      )}

      {tab === 'kyc' && (
        <div className="space-y-3 text-xs">
          {masters.map((m) => (
            <div key={m.userId} className="bg-surface border border-white/10 rounded-2xl p-4 flex flex-wrap items-center justify-between gap-3">
              <div><span className="font-bold text-white">{m.tashkentDistrict}</span> · <span className="text-gold">{m.kycStatus}</span></div>
              <div className="flex gap-2">
                <button onClick={async () => { await reviewMasterKyc(m.userId, 'VERIFIED'); setNotice('KYC tasdiqlandi'); refresh(); }} className="px-3 py-1.5 rounded-xl bg-emerald-500/20 text-emerald-300 font-bold">Tasdiqlash</button>
                <button onClick={async () => { await reviewMasterKyc(m.userId, 'REJECTED'); setNotice('KYC rad etildi'); refresh(); }} className="px-3 py-1.5 rounded-xl bg-rose-500/20 text-rose-300 font-bold">Rad etish</button>
              </div>
            </div>
          ))}
        </div>
      )}

      {tab === 'settings' && (
        <form onSubmit={onSaveContacts} className="bg-surface border border-gold/20 rounded-2xl p-5 grid sm:grid-cols-2 gap-3 text-xs">
          <div>
            <label className="block text-gray-400 mb-1">24/7 Call-markaz</label>
            <input value={phone} onChange={(e) => setPhone(e.target.value)} className="w-full bg-obsidian border border-white/15 rounded-xl px-3 py-2 text-white" />
          </div>
          <div>
            <label className="block text-gray-400 mb-1">Telegram havola</label>
            <input value={telegram} onChange={(e) => setTelegram(e.target.value)} className="w-full bg-obsidian border border-white/15 rounded-xl px-3 py-2 text-white" />
          </div>
          <div className="sm:col-span-2 flex justify-end">
            <button type="submit" className="gold-gradient text-obsidian font-bold px-5 py-2 rounded-xl">Bazada saqlash</button>
          </div>
        </form>
      )}
    </div>
  );
}
