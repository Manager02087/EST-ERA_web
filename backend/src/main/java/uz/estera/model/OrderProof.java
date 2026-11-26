package uz.estera.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_proofs")
public class OrderProof {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "order_id", nullable = false) private UUID orderId;
    @Column(name = "uploaded_by", nullable = false) private UUID uploadedBy;
    @Column(name = "object_key", nullable = false) private String objectKey;
    @Column(name = "original_filename", nullable = false) private String originalFilename;
    @Column(name = "mime_type", nullable = false) private String mimeType;
    @Column(name = "file_size_bytes", nullable = false) private long fileSizeBytes;
    @Column(name = "proof_type", nullable = false) private String proofType; // PHOTO, VIDEO, REPORT_TXT
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public UUID getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(UUID uploadedBy) { this.uploadedBy = uploadedBy; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    public String getProofType() { return proofType; }
    public void setProofType(String proofType) { this.proofType = proofType; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
