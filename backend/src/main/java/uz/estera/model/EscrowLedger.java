package uz.estera.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "escrow_ledger")
public class EscrowLedger {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "order_id", nullable = false) private UUID orderId;
    @Column(name = "entry_type", nullable = false) private String entryType;
    @Column(name = "amount_uzs", nullable = false) private long amountUzs;
    @Column(nullable = false) private String provider;
    @Column(name = "external_tx_id") private String externalTxId;
    @Column(name = "created_by") private UUID createdBy;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public long getAmountUzs() { return amountUzs; }
    public void setAmountUzs(long amountUzs) { this.amountUzs = amountUzs; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public String getExternalTxId() { return externalTxId; }
    public void setExternalTxId(String externalTxId) { this.externalTxId = externalTxId; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
