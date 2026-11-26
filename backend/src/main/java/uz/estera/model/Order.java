package uz.estera.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "order_code", nullable = false, unique = true)
    private String orderCode;
    @Column(name = "client_id", nullable = false)
    private UUID clientId;
    @Column(name = "master_id")
    private UUID masterId;
    @Column(name = "service_id", nullable = false)
    private Integer serviceId;
    @Column(nullable = false)
    private String status = "CREATED";
    @Column(name = "tashkent_district", nullable = false)
    private String tashkentDistrict;
    @Column(name = "address_text", nullable = false)
    private String addressText;
    @Column(name = "client_lat")
    private Double clientLat;
    @Column(name = "client_lng")
    private Double clientLng;
    @Column(name = "master_start_lat")
    private Double masterStartLat;
    @Column(name = "master_start_lng")
    private Double masterStartLng;
    @Column(name = "master_current_lat")
    private Double masterCurrentLat;
    @Column(name = "master_current_lng")
    private Double masterCurrentLng;
    @Column(name = "base_amount_uzs", nullable = false)
    private long baseAmountUzs;
    @Column(name = "discount_uzs", nullable = false)
    private long discountUzs = 0L;
    @Column(name = "total_amount_uzs", nullable = false)
    private long totalAmountUzs;
    @Column(name = "platform_fee_uzs", nullable = false)
    private long platformFeeUzs = 0L;
    @Column(name = "master_payout_uzs", nullable = false)
    private long masterPayoutUzs = 0L;
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod = "TEST_MODE";
    @Column(name = "support_chat_access_granted", nullable = false)
    private boolean supportChatAccessGranted = false;
    @Column(name = "client_confirmed_at")
    private OffsetDateTime clientConfirmedAt;
    @Column(name = "master_confirmed_at")
    private OffsetDateTime masterConfirmedAt;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; }
    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }
    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    public UUID getMasterId() { return masterId; }
    public void setMasterId(UUID masterId) { this.masterId = masterId; }
    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTashkentDistrict() { return tashkentDistrict; }
    public void setTashkentDistrict(String tashkentDistrict) { this.tashkentDistrict = tashkentDistrict; }
    public String getAddressText() { return addressText; }
    public void setAddressText(String addressText) { this.addressText = addressText; }
    public Double getClientLat() { return clientLat; }
    public void setClientLat(Double clientLat) { this.clientLat = clientLat; }
    public Double getClientLng() { return clientLng; }
    public void setClientLng(Double clientLng) { this.clientLng = clientLng; }
    public Double getMasterStartLat() { return masterStartLat; }
    public void setMasterStartLat(Double masterStartLat) { this.masterStartLat = masterStartLat; }
    public Double getMasterStartLng() { return masterStartLng; }
    public void setMasterStartLng(Double masterStartLng) { this.masterStartLng = masterStartLng; }
    public Double getMasterCurrentLat() { return masterCurrentLat; }
    public void setMasterCurrentLat(Double masterCurrentLat) { this.masterCurrentLat = masterCurrentLat; }
    public Double getMasterCurrentLng() { return masterCurrentLng; }
    public void setMasterCurrentLng(Double masterCurrentLng) { this.masterCurrentLng = masterCurrentLng; }
    public long getBaseAmountUzs() { return baseAmountUzs; }
    public void setBaseAmountUzs(long baseAmountUzs) { this.baseAmountUzs = baseAmountUzs; }
    public long getDiscountUzs() { return discountUzs; }
    public void setDiscountUzs(long discountUzs) { this.discountUzs = discountUzs; }
    public long getTotalAmountUzs() { return totalAmountUzs; }
    public void setTotalAmountUzs(long totalAmountUzs) { this.totalAmountUzs = totalAmountUzs; }
    public long getPlatformFeeUzs() { return platformFeeUzs; }
    public void setPlatformFeeUzs(long platformFeeUzs) { this.platformFeeUzs = platformFeeUzs; }
    public long getMasterPayoutUzs() { return masterPayoutUzs; }
    public void setMasterPayoutUzs(long masterPayoutUzs) { this.masterPayoutUzs = masterPayoutUzs; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public boolean isSupportChatAccessGranted() { return supportChatAccessGranted; }
    public void setSupportChatAccessGranted(boolean v) { this.supportChatAccessGranted = v; }
    public OffsetDateTime getClientConfirmedAt() { return clientConfirmedAt; }
    public void setClientConfirmedAt(OffsetDateTime t) { this.clientConfirmedAt = t; }
    public OffsetDateTime getMasterConfirmedAt() { return masterConfirmedAt; }
    public void setMasterConfirmedAt(OffsetDateTime t) { this.masterConfirmedAt = t; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
