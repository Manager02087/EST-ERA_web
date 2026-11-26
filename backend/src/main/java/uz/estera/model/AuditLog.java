package uz.estera.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "actor_id") private UUID actorId;
    @Column(name = "actor_role") private String actorRole;
    @Column(nullable = false) private String action;
    @Column(name = "target_entity") private String targetEntity;
    @Column(name = "target_id") private String targetId;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getId() { return id; }
    public UUID getActorId() { return actorId; }
    public void setActorId(UUID actorId) { this.actorId = actorId; }
    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getTargetEntity() { return targetEntity; }
    public void setTargetEntity(String targetEntity) { this.targetEntity = targetEntity; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
