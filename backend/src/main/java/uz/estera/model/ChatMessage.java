package uz.estera.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "order_id", nullable = false) private UUID orderId;
    @Column(name = "sender_id", nullable = false) private UUID senderId;
    @Column(nullable = false) private String body;
    @Column(name = "is_read", nullable = false) private boolean read = false;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public boolean isRead() { return read; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
