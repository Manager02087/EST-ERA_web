import type { Metadata } from 'next';
import Link from 'next/link';
import './globals.css';

export const metadata: Metadata = {
  title: 'EST-ERA — 24/7 VIP Service Marketplace (UZ / RU / EN)',
  description: "Toshkent bo'ylab 20 ta xizmat, 3 bosqichli Escrow himoyasi va 24/7 uzluksiz web + mobil ekotizim.",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="uz" className="dark">
      <body className="min-h-screen flex flex-col bg-obsidian text-gray-100 antialiased">
        <header className="sticky top-0 z-40 bg-obsidian/95 backdrop-blur border-b border-gold/20">
          <div className="max-w-7xl mx-auto px-4 py-3 flex flex-wrap items-center justify-between gap-3">
            <Link href="/" className="flex items-center gap-2.5">
              <img src="/brand/favicon-64.png" alt="EST-ERA" className="w-9 h-9 rounded-xl border border-gold/40" />
              <div>
                <div className="font-extrabold text-gold tracking-wide leading-none">EST-ERA</div>
                <div className="text-[10px] text-gray-400 uppercase tracking-widest mt-0.5">VIP 24/7</div>
              </div>
            </Link>
            <nav className="flex flex-wrap items-center gap-1.5 text-xs sm:text-sm font-semibold">
              <Link href="/" className="px-3 py-1.5 rounded-xl hover:bg-white/5">Xizmatlar</Link>
              <Link href="/masters" className="px-3 py-1.5 rounded-xl hover:bg-white/5">Ustalar &amp; Promo</Link>
              <Link href="/orders" className="px-3 py-1.5 rounded-xl hover:bg-white/5">Buyurtmalar &amp; Chat</Link>
              <Link href="/admin" className="px-3 py-1.5 rounded-xl hover:bg-white/5 text-gold">Admin (9 rol)</Link>
              <Link href="/auth" className="px-3 py-1.5 rounded-xl gold-gradient text-obsidian font-bold">Kirish</Link>
            </nav>
          </div>
        </header>
        <main className="flex-1 max-w-7xl w-full mx-auto px-4 py-8">{children}</main>
        <footer className="border-t border-white/10 bg-[#08090C] py-6 text-xs text-gray-400">
          <div className="max-w-7xl mx-auto px-4 flex flex-wrap items-center justify-between gap-3">
            <span>© 2026 EST-ERA — 24/7 VIP Service Marketplace</span>
            <div className="flex gap-4">
              <Link href="/legal" className="hover:text-gold">Ommaviy oferta &amp; Escrow</Link>
              <Link href="/admin" className="hover:text-gold">Admin Console</Link>
            </div>
          </div>
        </footer>
      </body>
    </html>
  );
}
