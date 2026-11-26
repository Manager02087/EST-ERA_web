package uz.estera.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.estera.model.ServiceCategory;
import uz.estera.model.SiteSetting;
import uz.estera.repository.ServiceCategoryRepository;
import uz.estera.repository.SiteSettingRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
public class PublicCatalogController {
    private final ServiceCategoryRepository services;
    private final SiteSettingRepository settings;

    public PublicCatalogController(ServiceCategoryRepository services, SiteSettingRepository settings) {
        this.services = services;
        this.settings = settings;
    }

    @GetMapping("/services")
    public List<ServiceCategory> getServices() {
        return services.findByActiveTrueOrderBySortOrderAsc();
    }

    @GetMapping("/settings")
    public List<SiteSetting> getSettings() {
        return settings.findAll();
    }
}
