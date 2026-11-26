package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.PromoCode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PromoCodeRepository extends JpaRepository<PromoCode, UUID> {
    Optional<PromoCode> findByCodeIgnoreCaseAndActiveTrue(String code);
    List<PromoCode> findByMasterIdOrderByCreatedAtDesc(UUID masterId);
}
