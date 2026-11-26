package uz.estera.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.estera.model.OrderProof;
import uz.estera.repository.OrderProofRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class ProofStorageService {
    public static final long MAX_TOTAL_ORDER_BYTES = 41_943_040L; // 40 MB exact
    private final OrderProofRepository proofs;
    private final Path rootDir;

    public ProofStorageService(
            OrderProofRepository proofs,
            @Value("${estera.storage.local-dir:/tmp/estera-proofs}") String localDir
    ) {
        this.proofs = proofs;
        this.rootDir = Path.of(localDir);
    }

    public OrderProof storeProof(UUID orderId, UUID uploaderId, String proofType, MultipartFile file) throws IOException {
        List<OrderProof> existing = proofs.findByOrderIdOrderByCreatedAtAsc(orderId);
        long usedBytes = existing.stream().mapToLong(OrderProof::getFileSizeBytes).sum();
        if (usedBytes + file.getSize() > MAX_TOTAL_ORDER_BYTES) {
            throw new IllegalArgumentException("Order proof limit exceeded: max 40 MB (41,943,040 bytes) total per order.");
        }
        String normalizedType = proofType.toUpperCase();
        if (!normalizedType.equals("PHOTO") && !normalizedType.equals("VIDEO") && !normalizedType.equals("REPORT_TXT")) {
            throw new IllegalArgumentException("proofType must be PHOTO, VIDEO, or REPORT_TXT");
        }
        String safeName = UUID.randomUUID() + "_" + (file.getOriginalFilename() != null ? file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_") : "proof.bin");
        Path orderDir = rootDir.resolve(orderId.toString());
        Files.createDirectories(orderDir);
        Path target = orderDir.resolve(safeName);
        file.transferTo(target);

        OrderProof p = new OrderProof();
        p.setOrderId(orderId);
        p.setUploadedBy(uploaderId);
        p.setObjectKey("proofs/" + orderId + "/" + safeName);
        p.setOriginalFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : safeName);
        p.setMimeType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        p.setFileSizeBytes(file.getSize());
        p.setProofType(normalizedType);
        return proofs.save(p);
    }

    public long usedBytes(UUID orderId) {
        return proofs.findByOrderIdOrderByCreatedAtAsc(orderId).stream().mapToLong(OrderProof::getFileSizeBytes).sum();
    }
}
