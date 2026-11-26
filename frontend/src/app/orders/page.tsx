'use client';

import { FormEvent, useEffect, useState } from 'react';
import {
  cancelOrder, ChatMsg, confirmOrder, createOrder, getMyOrders, getOrderChat,
  getOrderProofs, grantSupportChat, openOrderDispute, OrderItem, sendOrderChat,
  uploadOrderProof, validatePromo,
} from '@/lib/api';

const MAX_MB = 40;

export default function OrdersWorkspacePage() {
  const [orders, setOrders] = useState<OrderItem[]>([]);
  const [selected, setSelected] = useState<OrderItem | null>(null);
  const [chat, setChat] = useState<ChatMsg[]>([]);
  const [msgText, setMsgText] = useState('');
  const [proofsInfo, setProofsInfo] = useState<{ items: { id: string; originalFilename: string; proofType: string; fileSizeBytes: number }[]; usedBytes: number; maxBytes: number } | null>(null);
  const [proofType, setProofType] = useState<'PHOTO' | 'VIDEO' | 'REPORT_TXT'>('PHOTO');
  const [statusMsg, setStatusMsg] = useState('');

  // New Order Form State
  const [district, setDistrict] = useState('Chilonzor');
  const [address, setAddress] = useState('');
  const [amount, setAmount] = useState(250000);
  const [promo, setPromo] = useState('');
  const [finalAmount, setFinalAmount] = useState(250000);

  const loadOrders = () => getMyOrders().then(setOrders).catch(() => setOrders([]));
  useEffect(() => { loadOrders(); }, []);

  async function selectOrder(o: OrderItem) {
    setSelected(o);
    getOrderChat(o.id).then(setChat).catch(() => setChat([]));
    getOrderProofs(o.id).then(setProofsInfo).catch(() => setProofsInfo(null));
  }

  async function onCheckPromo() {
    try {
      const res = await validatePromo(promo, amount);
      setFinalAmount(res.finalAmountUzs);
      setStatusMsg(`Promo qo'llandi! Chegirma: ${res.discountUzs.toLocaleString('ru-RU')} so'm`);
    } catch {
      setStatusMsg('Promo-kod yaroqsiz yoki muddati tugagan.');
    }
  }

  async function onCreateOrder(e: FormEvent) {
    e.preventDefault();
    try {
      const created = await createOrder({ serviceId: 1, tashkentDistrict: district, addressText: address, amountUzs: finalAmount, paymentMethod: 'TEST_MODE' });
      setStatusMsg(`Buyurtma ${created.orderCode} Escrow himoyasi bilan yaratildi!`);
      setAddress('');
      loadOrders();
    } catch (err) {
      setStatusMsg(`Avval tizimga kiring: ${(err as Error).message}`);
    }
  }

  async function onSendChat(e: FormEvent) {
    e.preventDefault();
    if (!selected || !msgText.trim()) return;
    await sendOrderChat(selected.id, msgText.trim());
    setMsgText('');
    selectOrder(selected);
  }

  async function onUploadProof(file: File | null) {
    if (!selected || !file) return;
    try {
      await uploadOrderProof(selected.id, proofType, file);
      setStatusMsg(`Isbot fayli (${proofType}) yuklandi!`);
      selectOrder(selected);
    } catch (err) {
      setStatusMsg(`Yuklash xatosi: ${(err as Error).message}`);
    }
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h1 className="text-2xl font-extrabold text-white">Buyurtmalar, 40 MB Isbotlar va Jonli Chat</h1>
        {statusMsg && <span className="text-xs px-3 py-1.5 rounded-xl bg-gold/15 text-gold font-semibold">{statusMsg}</span>}
      </div>

      {/* Create Order + Promo + 2%/98% Escrow */}
      <form onSubmit={onCreateOrder} className="bg-surface border border-gold/25 rounded-3xl p-6 grid sm:grid-cols-2 lg:grid-cols-5 gap-3 text-xs">
        <input value={district} onChange={(e) => setDistrict(e.target.value)} placeholder="Tuman" className="bg-obsidian border border-white/15 rounded-xl px-3 py-2.5 text-white" />
        <input required value={address} onChange={(e) => setAddress(e.target.value)} placeholder="Aniq manzil" className="bg-obsidian border border-white/15 rounded-xl px-3 py-2.5 text-white" />
        <input type="number" min={50000} value={amount} onChange={(e) => { const v = Number(e.target.value); setAmount(v); setFinalAmount(v); }} className="bg-obsidian border border-white/15 rounded-xl px-3 py-2.5 text-white" />
        <div className="flex gap-1.5">
          <input value={promo} onChange={(e) => setPromo(e.target.value)} placeholder="Promo-kod" className="w-full bg-obsidian border border-white/15 rounded-xl px-3 py-2.5 text-white uppercase" />
          <button type="button" onClick={onCheckPromo} className="px-3 rounded-xl bg-white/10 text-gold font-bold">OK</button>
        </div>
        <button type="submit" className="gold-gradient text-obsidian font-bold rounded-xl py-2.5">
          Buyurtma ({finalAmount.toLocaleString('ru-RU')} so&apos;m)
        </button>
      </form>

      <div className="grid lg:grid-cols-12 gap-6">
        {/* Orders List */}
        <div className="lg:col-span-5 space-y-2.5">
          <h2 className="text-sm font-bold text-gray-300">Mening buyurtmalarim</h2>
          {orders.length === 0 ? (
            <div className="bg-surface border border-white/10 rounded-2xl p-4 text-xs text-gray-400">Hozircha buyurtma yo&apos;q.</div>
          ) : orders.map((o) => (
            <button key={o.id} onClick={() => selectOrder(o)} className={`w-full text-left p-4 rounded-2xl border transition ${selected?.id === o.id ? 'bg-gold/10 border-gold' : 'bg-surface border-white/10 hover:border-white/25'}`}>
              <div className="flex justify-between items-center">
                <span className="font-mono text-xs font-bold text-gold">{o.orderCode}</span>
                <span className="text-[11px] px-2 py-0.5 rounded-lg bg-emerald-500/15 text-emerald-400 font-semibold">{o.status}</span>
              </div>
              <div className="text-xs text-gray-300 mt-1">{o.tashkentDistrict} — {o.addressText}</div>
              <div className="text-xs font-bold text-white mt-1">{o.totalAmountUzs.toLocaleString('ru-RU')} so&apos;m</div>
            </button>
          ))}
        </div>

        {/* Selected Order Detail: 40MB Proofs + Chat + Actions */}
        <div className="lg:col-span-7 space-y-4">
          {!selected ? (
            <div className="bg-surface border border-white/10 rounded-2xl p-6 text-xs text-gray-400">Tafsilotlar, 40 MB isbot yuklash va chatni ochish uchun chap tomondan buyurtmani tanlang.</div>
          ) : (
            <>
              <div className="bg-surface border border-gold/20 rounded-2xl p-5 space-y-3">
                <div className="flex flex-wrap justify-between items-center gap-2">
                  <div className="font-bold text-white">{selected.orderCode} · {selected.status}</div>
                  <div className="flex flex-wrap gap-2 text-xs">
                    <button onClick={async () => { await confirmOrder(selected.id); setStatusMsg('Tasdiqlandi!'); loadOrders(); }} className="px-3 py-1.5 rounded-xl bg-emerald-500/20 text-emerald-300 font-bold">Ikki tomonlama tasdiqlash</button>
                    <button onClick={async () => { await grantSupportChat(selected.id); setStatusMsg('Supportga chat ruxsati berildi!'); }} className="px-3 py-1.5 rounded-xl bg-gold/15 text-gold font-bold">Supportga chat ruxsati</button>
<button onClick={async () => { await openOrderDispute(selected.id, "Sifat/vaqt bo'yicha murojaat"); setStatusMsg('Nizo ochildi (SUPPORT_L2)'); loadOrders(); }} className="px-3 py-1.5 rounded-xl bg-amber-500/20 text-amber-300 font-bold">Nizo ochish</button>                    <button onClick={async () => { await cancelOrder(selected.id); setStatusMsg('Bekor qilindi (GPS/Escrow qoidasi)'); loadOrders(); }} className="px-3 py-1.5 rounded-xl bg-rose-500/20 text-rose-300 font-bold">Bekor qilish</button>
                  </div>
                </div>

                {/* 40 MB Proof Uploader */}
                <div className="pt-3 border-t border-white/10 space-y-2 text-xs">
                  <div className="flex justify-between text-gray-300">
                    <span className="font-bold">Isbot fayllari (Foto + Video + report.txt)</span>
                    <span className="text-gold">{((proofsInfo?.usedBytes || 0) / 1048576).toFixed(2)} / {MAX_MB} MB</span>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    <select value={proofType} onChange={(e) => setProofType(e.target.value as 'PHOTO' | 'VIDEO' | 'REPORT_TXT')} className="bg-obsidian border border-white/15 rounded-xl px-3 py-1.5 text-white">
                      <option value="PHOTO">PHOTO</option>
                      <option value="VIDEO">VIDEO</option>
                      <option value="REPORT_TXT">report.txt</option>
                    </select>
                    <input type="file" onChange={(e) => onUploadProof(e.target.files?.[0] || null)} className="text-xs text-gray-300" />
                  </div>
                </div>
              </div>

              {/* Order-Scoped STOMP / REST Chat */}
              <div className="bg-surface border border-white/10 rounded-2xl p-5 space-y-3">
                <div className="text-sm font-bold text-white">Buyurtma chati (Mijoz ↔ Usta)</div>
                <div className="h-48 overflow-y-auto space-y-2 bg-obsidian rounded-xl p-3 text-xs">
                  {chat.length === 0 ? <div className="text-gray-500">Xabarlar hali yo&apos;q.</div> : chat.map((m) => (
                    <div key={m.id} className="bg-surfaceLight rounded-xl px-3 py-2 text-gray-200">{m.body}</div>
                  ))}
                </div>
                <form onSubmit={onSendChat} className="flex gap-2">
                  <input value={msgText} onChange={(e) => setMsgText(e.target.value)} placeholder="Xabar yozing..." className="flex-1 bg-obsidian border border-white/15 rounded-xl px-3 py-2 text-xs text-white" />
                  <button type="submit" className="gold-gradient text-obsidian font-bold px-4 py-2 rounded-xl text-xs">Yuborish</button>
                </form>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
