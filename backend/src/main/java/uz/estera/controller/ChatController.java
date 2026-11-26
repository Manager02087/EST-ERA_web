package uz.estera.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uz.estera.model.ChatMessage;
import uz.estera.model.Order;
import uz.estera.repository.ChatMessageRepository;
import uz.estera.repository.OrderRepository;

import java.util.Map;
import java.util.UUID;

@RestController
public class ChatController {
    private final ChatMessageRepository messages;
    private final OrderRepository orders;

    public ChatController(ChatMessageRepository messages, OrderRepository orders) {
        this.messages = messages;
        this.orders = orders;
    }

    public record ChatPayload(UUID senderId, String body) {}

    @MessageMapping("/chat/{orderId}")
    @SendTo("/topic/orders/{orderId}")
    public ChatMessage handleStompMessage(@DestinationVariable UUID orderId, ChatPayload payload) {
        ChatMessage m = new ChatMessage();
        m.setOrderId(orderId);
        m.setSenderId(payload.senderId());
        m.setBody(payload.body());
        return messages.save(m);
    }

    @GetMapping("/api/v1/orders/{orderId}/chat")
    public ResponseEntity<?> getChatHistory(@PathVariable UUID orderId, Authentication auth) {
        Order order = orders.findById(orderId).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        UUID uid = UUID.fromString(auth.getName());
        boolean isParticipant = uid.equals(order.getClientId()) || uid.equals(order.getMasterId());
        boolean isSuperAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        // EST-ERA Spec: Support can read full chat history ONLY if at least one party granted access
        if (!isParticipant && !isSuperAdmin && !order.isSupportChatAccessGranted()) {
            return ResponseEntity.status(403).body(Map.of(
                    "error", "Support chat access not granted yet. Either Client or Master must enable access."
            ));
        }
        return ResponseEntity.ok(messages.findByOrderIdOrderByCreatedAtAsc(orderId));
    }

    @PostMapping("/api/v1/orders/{orderId}/chat")
    public ChatMessage postRestMessage(@PathVariable UUID orderId, @RequestBody Map<String, String> body, Authentication auth) {
        ChatMessage m = new ChatMessage();
        m.setOrderId(orderId);
        m.setSenderId(UUID.fromString(auth.getName()));
        m.setBody(body.getOrDefault("body", ""));
        return messages.save(m);
    }
}
