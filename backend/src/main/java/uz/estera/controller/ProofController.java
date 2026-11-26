package uz.estera.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.estera.model.OrderProof;
import uz.estera.repository.OrderProofRepository;
import uz.estera.service.ProofStorageService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/proofs")
public class ProofController {
    private final ProofStorageService storage;
    private final OrderProofRepository proofs;

    public ProofController(ProofStorageService storage, OrderProofRepository proofs) {
        this.storage = storage;
        this.proofs = proofs;
    }

    @PostMapping
    public ResponseEntity<?> uploadProof(
            @PathVariable UUID orderId,
            @RequestParam("proofType") String proofType,
            @RequestParam("file") MultipartFile file,
            Authentication auth
    ) {
        try {
            OrderProof saved = storage.storeProof(orderId, UUID.fromString(auth.getName()), proofType, file);
            long used = storage.usedBytes(orderId);
            return ResponseEntity.ok(Map.of(
                    "proof", saved,
                    "usedBytes", used,
                    "remainingBytes", ProofStorageService.MAX_TOTAL_ORDER_BYTES - used
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public Map<String, Object> listProofs(@PathVariable UUID orderId) {
        List<OrderProof> list = proofs.findByOrderIdOrderByCreatedAtAsc(orderId);
        long used = list.stream().mapToLong(OrderProof::getFileSizeBytes).sum();
        return Map.of(
                "items", list,
                "usedBytes", used,
                "maxBytes", ProofStorageService.MAX_TOTAL_ORDER_BYTES,
                "remainingBytes", ProofStorageService.MAX_TOTAL_ORDER_BYTES - used
        );
    }
}
