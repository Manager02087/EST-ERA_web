package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.OrderProof;
import java.util.List;
import java.util.UUID;

public interface OrderProofRepository extends JpaRepository<OrderProof, UUID> {
    List<OrderProof> findByOrderIdOrderByCreatedAtAsc(UUID orderId);
}
