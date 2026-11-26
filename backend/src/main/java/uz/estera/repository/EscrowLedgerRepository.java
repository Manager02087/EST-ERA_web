package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.EscrowLedger;
import java.util.List;
import java.util.UUID;

public interface EscrowLedgerRepository extends JpaRepository<EscrowLedger, UUID> {
    List<EscrowLedger> findAllByOrderByCreatedAtDesc();
}
