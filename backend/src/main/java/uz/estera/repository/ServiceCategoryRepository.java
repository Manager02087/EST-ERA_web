package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.ServiceCategory;
import java.util.List;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Integer> {
    List<ServiceCategory> findByActiveTrueOrderBySortOrderAsc();
}
