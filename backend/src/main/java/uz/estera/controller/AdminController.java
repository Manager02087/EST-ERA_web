package uz.estera.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.estera.model.*;
import uz.estera.repository.*;
import uz.estera.security.UserRole;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final OrderRepository orders;
    private final SiteSettingRepository settings;
    private final UserRepository users;
    private final DisputeRepository disputes;
    private final EscrowLedgerRepository ledger;
    private final AuditLogRepository auditLogs;

    public AdminController(
            OrderRepository orders, SiteSettingRepository settings, UserRepository users,
            DisputeRepository disputes, EscrowLedgerRepository ledger, AuditLogRepository auditLogs
    ) {
        this.orders = orders;
        this.settings = settings;
        this.users = users;
        this.disputes = disputes;
        this.ledger = ledger;
        this.auditLogs = auditLogs;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<Order> allOrders = orders.findAll();
        long totalEscrowVolume = allOrders.stream().mapToLong(Order::getTotalAmountUzs).sum();
        long platformCommission2Pct = allOrders.stream().mapToLong(Order::getPlatformFeeUzs).sum();
        long openDisputes = disputes.findAll().stream().filter(d -> "OPEN".equals(d.getStatus()) || "INVESTIGATING".equals(d.getStatus())).count();
        return Map.of(
                "totalUsers", users.count(),
                "totalOrders", allOrders.size(),
                "totalEscrowVolumeUzs", totalEscrowVolume,
                "platformCommission2PctUzs", platformCommission2Pct,
                "openDisputes", openDisputes
        );
    }

    @GetMapping("/orders")
    public List<Order> allOrders() {
        return orders.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/ledger")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_S2')")
    public List<EscrowLedger> escrowLedger() {
        return ledger.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_S1','ADMIN_S3')")
    public List<AuditLog> recentAuditLogs() {
        return auditLogs.findTop100ByOrderByCreatedAtDesc();
    }

    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public User assignRole(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        User u = users.findById(id).orElseThrow();
        u.setRole(UserRole.valueOf(body.get("role")));
        return users.save(u);
    }

    @PutMapping("/settings/{key}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_S1','ADMIN_S3')")
    public SiteSetting updateSetting(@PathVariable String key, @RequestBody Map<String, String> body) {
        SiteSetting s = settings.findById(key).orElseGet(() -> {
            SiteSetting created = new SiteSetting();
            created.setKey(key);
            return created;
        });
        s.setValueUz(body.getOrDefault("uz", ""));
        s.setValueRu(body.getOrDefault("ru", ""));
        s.setValueEn(body.getOrDefault("en", ""));
        s.setUpdatedAt(OffsetDateTime.now());
        return settings.save(s);
    }
}
