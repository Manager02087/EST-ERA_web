package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.Dispute;
import java.util.List;
import java.util.UUID;

public interface DisputeRepository extends JpaRepository<Dispute, UUID> {
    List<Dispute> findAllByOrderByCreatedAtDesc();
}
