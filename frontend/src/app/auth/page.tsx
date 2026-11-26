'use client';

import { FormEvent, useState } from 'react';
import { loginUser, registerUser } from '@/lib/api';

export default function AuthPage() {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [phone, setPhone] = useState('+998');
  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState<'CLIENT' | 'MASTER'>('CLIENT');
  const [status, setStatus] = useState('');

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setStatus('Yuklanmoqda...');
    try {
      if (mode === 'login') {
        const res = await loginUser(phone, password);
        localStorage.setItem('estera_jwt', res.token);
        setStatus(`Xush kelibsiz, ${res.fullName} (${res.role})`);
      } else {
        const res = await registerUser({ phone, fullName, password, role, preferredLang: 'uz' });
        localStorage.setItem('estera_jwt', res.token);
        setStatus(`Ro'yxatdan o'tildi! Rol: ${res.role}`);
      }
    } catch (err) {
      setStatus(`Xatolik: ${(err as Error).message}`);
    }
  }

  return (
    <div className="max-w-md mx-auto bg-surface border border-gold/25 rounded-3xl p-6 space-y-5">
      <div className="flex gap-2 bg-obsidian p-1 rounded-xl">
        <button onClick={() => setMode('login')} className={`flex-1 py-2 rounded-lg text-xs font-bold ${mode === 'login' ? 'gold-gradient text-obsidian' : 'text-gray-400'}`}>Kirish</button>
        <button onClick={() => setMode('register')} className={`flex-1 py-2 rounded-lg text-xs font-bold ${mode === 'register' ? 'gold-gradient text-obsidian' : 'text-gray-400'}`}>Ro&apos;yxatdan o&apos;tish</button>
      </div>
      <form onSubmit={onSubmit} className="space-y-3 text-sm">
        {mode === 'register' && (
          <>
            <input required value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Ism-familiya" className="w-full bg-obsidian border border-white/10 rounded-xl px-3.5 py-2.5 text-white" />
            <select value={role} onChange={(e) => setRole(e.target.value as 'CLIENT' | 'MASTER')} className="w-full bg-obsidian border border-white/10 rounded-xl px-3.5 py-2.5 text-white">
              <option value="CLIENT">Mijoz (Web + Flutter)</option>
              <option value="MASTER">Usta (Web + Flutter)</option>
            </select>
          </>
        )}
        <input required value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="+998901234567" className="w-full bg-obsidian border border-white/10 rounded-xl px-3.5 py-2.5 text-white" />
        <input required type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Parol" className="w-full bg-obsidian border border-white/10 rounded-xl px-3.5 py-2.5 text-white" />
        <button type="submit" className="w-full gold-gradient text-obsidian font-bold py-2.5 rounded-xl">
          {mode === 'login' ? 'Kirish' : 'Akkaunt yaratish'}
        </button>
      </form>
      {status && <p className="text-xs text-gold text-center">{status}</p>}
    </div>
  );
}
