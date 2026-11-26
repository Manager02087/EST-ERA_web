package uz.estera.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.estera.model.SiteSetting;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, String> {}
