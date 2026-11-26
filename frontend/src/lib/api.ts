const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface ServiceItem {
  id: number; slug: string; nameUz: string; nameRu: string; nameEn: string;
  descUz: string; descRu: string; descEn: string; groupKey: string; iconKey: string; basePriceUzs: number;
}
export interface MasterItem {
  userId: string; bioUz?: string; bioRu?: string; bioEn?: string; experienceYears: number;
  tashkentDistrict: string; ratingAvg: number; completedOrders: number; hourlyRateUzs: number;
  kycStatus: string; online24x7: boolean;
}
export interface OrderItem {
  id: string; orderCode: string; serviceId: number; status: string; tashkentDistrict: string;
  addressText: string; totalAmountUzs: number; platformFeeUzs: number; masterPayoutUzs: number;
  paymentMethod: string; supportChatAccessGranted: boolean; clientConfirmedAt?: string; masterConfirmedAt?: string;
}
export interface ChatMsg { id: string; orderId: string; senderId: string; body: string; createdAt: string; }
export interface DisputeItem { id: string; orderId: string; escalationTier: string; reason: string; status: string; resolutionNotes?: string; }
export interface SiteSettingItem { key: string; valueUz: string; valueRu: string; valueEn: string; }

export async function apiFetch<T>(path: string, init: RequestInit = {}): Promise<T> {
  const token = typeof window !== 'undefined' ? localStorage.getItem('estera_jwt') : null;
  const isForm = init.body instanceof FormData;
  const headers: Record<string, string> = {
    ...(isForm ? {} : { 'Content-Type': 'application/json' }),
    ...(init.headers as Record<string, string>),
  };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, { ...init, headers });
  if (!res.ok) throw new Error((await res.text()) || `HTTP ${res.status}`);
  return res.json() as Promise<T>;
}

// Public & Auth
export const getPublicServices = () => apiFetch<ServiceItem[]>('/api/v1/public/services');
export const getPublicMasters = (district = 'all', onlineOnly = false) =>
  apiFetch<MasterItem[]>(`/api/v1/public/masters?district=${encodeURIComponent(district)}&onlineOnly=${onlineOnly}`);
export const getPublicSettings = () => apiFetch<SiteSettingItem[]>('/api/v1/public/settings');
export const loginUser = (phone: string, password: string) =>
  apiFetch<{ token: string; userId: string; fullName: string; role: string }>('/api/v1/auth/login', {
    method: 'POST', body: JSON.stringify({ phone, password }),
  });
export const registerUser = (payload: { phone: string; fullName: string; password: string; role: 'CLIENT' | 'MASTER'; preferredLang: 'uz' | 'ru' | 'en' }) =>
  apiFetch<{ token: string; userId: string; role: string }>('/api/v1/auth/register', { method: 'POST', body: JSON.stringify(payload) });

// Promo Codes
export const createPromo = (payload: { code: string; discountType: 'PERCENT' | 'FIXED_UZS'; discountValue: number; validHours: number; maxUses: number }) =>
  apiFetch('/api/v1/masters/promos', { method: 'POST', body: JSON.stringify(payload) });
export const validatePromo = (code: string, orderAmountUzs: number) =>
  apiFetch<{ valid: boolean; discountUzs: number; finalAmountUzs: number }>('/api/v1/promos/validate', {
    method: 'POST', body: JSON.stringify({ code, orderAmountUzs }),
  });

// Orders, 40MB Proofs, Chat & Disputes
export const getMyOrders = () => apiFetch<OrderItem[]>('/api/v1/orders/my');
export const createOrder = (payload: { serviceId: number; tashkentDistrict: string; addressText: string; amountUzs: number; paymentMethod: string }) =>
  apiFetch<OrderItem>('/api/v1/orders', { method: 'POST', body: JSON.stringify(payload) });
export const confirmOrder = (orderId: string) => apiFetch(`/api/v1/orders/${orderId}/confirm`, { method: 'POST' });
export const cancelOrder = (orderId: string) => apiFetch(`/api/v1/orders/${orderId}/cancel`, { method: 'POST' });
export const grantSupportChat = (orderId: string) => apiFetch(`/api/v1/orders/${orderId}/grant-support-chat`, { method: 'POST' });
export const uploadOrderProof = (orderId: string, proofType: 'PHOTO' | 'VIDEO' | 'REPORT_TXT', file: File) => {
  const fd = new FormData();
  fd.append('proofType', proofType);
  fd.append('file', file);
  return apiFetch<{ usedBytes: number; remainingBytes: number }>(`/api/v1/orders/${orderId}/proofs`, { method: 'POST', body: fd });
};
export const getOrderProofs = (orderId: string) =>
  apiFetch<{ items: { id: string; originalFilename: string; proofType: string; fileSizeBytes: number }[]; usedBytes: number; maxBytes: number }>(`/api/v1/orders/${orderId}/proofs`);
export const getOrderChat = (orderId: string) => apiFetch<ChatMsg[]>(`/api/v1/orders/${orderId}/chat`);
export const sendOrderChat = (orderId: string, body: string) =>
  apiFetch<ChatMsg>(`/api/v1/orders/${orderId}/chat`, { method: 'POST', body: JSON.stringify({ body }) });
export const openOrderDispute = (orderId: string, reason: string) =>
  apiFetch<DisputeItem>(`/api/v1/orders/${orderId}/disputes`, { method: 'POST', body: JSON.stringify({ reason }) });

// Admin (9-Role RBAC)
export const getAdminDashboard = () =>
  apiFetch<{ totalUsers: number; totalOrders: number; totalEscrowVolumeUzs: number; platformCommission2PctUzs: number; openDisputes: number }>('/api/v1/admin/dashboard');
export const getAdminOrders = () => apiFetch<OrderItem[]>('/api/v1/admin/orders');
export const getAdminDisputes = () => apiFetch<DisputeItem[]>('/api/v1/admin/disputes');
export const escalateDispute = (id: string) => apiFetch<DisputeItem>(`/api/v1/admin/disputes/${id}/escalate`, { method: 'POST' });
export const resolveDispute = (id: string, status: string, resolutionNotes: string) =>
  apiFetch<DisputeItem>(`/api/v1/admin/disputes/${id}/resolve`, { method: 'POST', body: JSON.stringify({ status, resolutionNotes }) });
export const reviewMasterKyc = (userId: string, kycStatus: 'VERIFIED' | 'REJECTED') =>
  apiFetch(`/api/v1/admin/masters/${userId}/kyc`, { method: 'PATCH', body: JSON.stringify({ kycStatus }) });
export const updateSiteSetting = (key: string, uz: string, ru: string, en: string) =>
  apiFetch<SiteSettingItem>(`/api/v1/admin/settings/${key}`, { method: 'PUT', body: JSON.stringify({ uz, ru, en }) });
