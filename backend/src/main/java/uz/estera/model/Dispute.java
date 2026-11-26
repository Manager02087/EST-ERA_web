package uz.estera.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "disputes")
public class Dispute {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "order_id", nullable = false, unique = true) private UUID orderId;
    @Column(name = "opened_by", nullable = false) private UUID openedBy;
    @Column(name = "assigned_staff_id") private UUID assignedStaffId;
    @Column(name = "escalation_tier", nullable = false) private String escalationTier = "SUPPORT_L2";
    @Column(nullable = false) private String reason;
    @Column(nullable = false) private String status = "OPEN";
    @Column(name = "resolution_notes") private String resolutionNotes;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name = "resolved_at") private OffsetDateTime resolvedAt;

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public UUID getOpenedBy() { return openedBy; }
    public void setOpenedBy(UUID openedBy) { this.openedBy = openedBy; }
    public UUID getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(UUID assignedStaffId) { this.assignedStaffId = assignedStaffId; }
    public String getEscalationTier() { return escalationTier; }
    public void setEscalationTier(String escalationTier) { this.escalationTier = escalationTier; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(OffsetDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
