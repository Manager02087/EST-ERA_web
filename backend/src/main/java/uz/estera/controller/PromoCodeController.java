package uz.estera.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uz.estera.model.PromoCode;
import uz.estera.repository.PromoCodeRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class PromoCodeController {
    private final PromoCodeRepository promos;

    public PromoCodeController(PromoCodeRepository promos) {
        this.promos = promos;
    }

    public record CreatePromoReq(String code, String discountType, long discountValue, int validHours, int maxUses) {}
    public record ValidatePromoReq(String code, long orderAmountUzs) {}

    @PostMapping("/api/v1/masters/promos")
    @PreAuthorize("hasRole('MASTER')")
    public PromoCode createPromo(@RequestBody CreatePromoReq req, Authentication auth) {
        PromoCode p = new PromoCode();
        p.setMasterId(UUID.fromString(auth.getName()));
        p.setCode(req.code().trim().toUpperCase());
        p.setDiscountType("PERCENT".equalsIgnoreCase(req.discountType()) ? "PERCENT" : "FIXED_UZS");
        p.setDiscountValue(req.discountValue());
        p.setValidUntil(OffsetDateTime.now().plusHours(Math.max(1, req.validHours())));
        p.setMaxUses(Math.max(1, req.maxUses()));
        return promos.save(p);
    }

    @GetMapping("/api/v1/masters/promos/my")
    @PreAuthorize("hasRole('MASTER')")
    public List<PromoCode> myPromos(Authentication auth) {
        return promos.findByMasterIdOrderByCreatedAtDesc(UUID.fromString(auth.getName()));
    }

    @PostMapping("/api/v1/promos/validate")
    public ResponseEntity<?> validatePromo(@RequestBody ValidatePromoReq req) {
        return promos.findByCodeIgnoreCaseAndActiveTrue(req.code().trim())
                .filter(p -> p.getValidUntil().isAfter(OffsetDateTime.now()) && p.getUsedCount() < p.getMaxUses())
                .map(p -> {
                    long discount = "PERCENT".equals(p.getDiscountType())
                            ? Math.round(req.orderAmountUzs() * (p.getDiscountValue() / 100.0))
                            : Math.min(req.orderAmountUzs(), p.getDiscountValue());
                    return ResponseEntity.ok(Map.of(
                            "valid", true,
                            "code", p.getCode(),
                            "discountUzs", discount,
                            "finalAmountUzs", Math.max(0, req.orderAmountUzs() - discount)
                    ));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("valid", false, "error", "Promo code invalid or expired")));
    }
}
