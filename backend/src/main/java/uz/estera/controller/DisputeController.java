package uz.estera.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uz.estera.model.AuditLog;
import uz.estera.model.Dispute;
import uz.estera.model.Order;
import uz.estera.repository.AuditLogRepository;
import uz.estera.repository.DisputeRepository;
import uz.estera.repository.OrderRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class DisputeController {
    private final DisputeRepository disputes;
    private final OrderRepository orders;
    private final AuditLogRepository auditLogs;

    public DisputeController(DisputeRepository disputes, OrderRepository orders, AuditLogRepository auditLogs) {
        this.disputes = disputes;
        this.orders = orders;
        this.auditLogs = auditLogs;
    }

    @PostMapping("/api/v1/orders/{orderId}/disputes")
    public ResponseEntity<?> openDispute(@PathVariable UUID orderId, @RequestBody Map<String, String> body, Authentication auth) {
        Order order = orders.findById(orderId).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();
        order.setStatus("DISPUTED");
        orders.save(order);

        Dispute d = new Dispute();
        d.setOrderId(orderId);
        d.setOpenedBy(UUID.fromString(auth.getName()));
        d.setReason(body.getOrDefault("reason", "Dispute opened"));
        d.setEscalationTier("SUPPORT_L2");
        return ResponseEntity.ok(disputes.save(d));
    }

    @GetMapping("/api/v1/admin/disputes")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_S3','SUPPORT_L2','SUPPORT_L3')")
    public List<Dispute> listDisputes() {
        return disputes.findAllByOrderByCreatedAtDesc();
    }

    @PostMapping("/api/v1/admin/disputes/{id}/escalate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_S3','SUPPORT_L2','SUPPORT_L3')")
    public ResponseEntity<?> escalate(@PathVariable UUID id, Authentication auth) {
        return disputes.findById(id).map(d -> {
            String next = "SUPPORT_L2".equals(d.getEscalationTier()) ? "SUPPORT_L3" : "ADMIN_S3";
            d.setEscalationTier(next);
            d.setStatus("INVESTIGATING");
            disputes.save(d);

            AuditLog log = new AuditLog();
            log.setActorId(UUID.fromString(auth.getName()));
            log.setAction("DISPUTE_ESCALATED_TO_" + next);
            log.setTargetEntity("disputes");
            log.setTargetId(id.toString());
            auditLogs.save(log);
            return ResponseEntity.ok(d);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/v1/admin/disputes/{id}/resolve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_S3','SUPPORT_L3')")
    public ResponseEntity<?> resolve(@PathVariable UUID id, @RequestBody Map<String, String> body, Authentication auth) {
        return disputes.findById(id).map(d -> {
            d.setStatus(body.getOrDefault("status", "RESOLVED_CLIENT"));
            d.setResolutionNotes(body.getOrDefault("resolutionNotes", ""));
            d.setAssignedStaffId(UUID.fromString(auth.getName()));
            d.setResolvedAt(OffsetDateTime.now());
            disputes.save(d);

            AuditLog log = new AuditLog();
            log.setActorId(UUID.fromString(auth.getName()));
            log.setAction("DISPUTE_RESOLVED_" + d.getStatus());
            log.setTargetEntity("disputes");
            log.setTargetId(id.toString());
            auditLogs.save(log);
            return ResponseEntity.ok(d);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
