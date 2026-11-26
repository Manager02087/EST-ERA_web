package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.Order;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByClientIdOrderByCreatedAtDesc(UUID clientId);
    List<Order> findByMasterIdOrderByCreatedAtDesc(UUID masterId);
    List<Order> findAllByOrderByCreatedAtDesc();
}
