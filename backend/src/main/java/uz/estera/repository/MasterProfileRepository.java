package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.MasterProfile;
import java.util.List;
import java.util.UUID;

public interface MasterProfileRepository extends JpaRepository<MasterProfile, UUID> {
    List<MasterProfile> findAllByOrderByRatingAvgDescCompletedOrdersDesc();
    List<MasterProfile> findByOnline24x7TrueOrderByRatingAvgDesc();
}
