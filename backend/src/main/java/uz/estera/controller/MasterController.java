package uz.estera.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import uz.estera.model.MasterProfile;
import uz.estera.repository.MasterProfileRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class MasterController {
    private final MasterProfileRepository masters;

    public MasterController(MasterProfileRepository masters) {
        this.masters = masters;
    }

    @GetMapping("/api/v1/public/masters")
    public List<MasterProfile> listMasters(
            @RequestParam(required = false) String district,
            @RequestParam(defaultValue = "false") boolean onlineOnly
    ) {
        List<MasterProfile> base = onlineOnly
                ? masters.findByOnline24x7TrueOrderByRatingAvgDesc()
                : masters.findAllByOrderByRatingAvgDescCompletedOrdersDesc();
        if (district != null && !district.isBlank() && !"all".equalsIgnoreCase(district)) {
            return base.stream().filter(m -> district.equalsIgnoreCase(m.getTashkentDistrict())).toList();
        }
        return base;
    }

    public record UpdateMasterReq(String bioUz, String bioRu, String bioEn, short experienceYears, String tashkentDistrict, long hourlyRateUzs, boolean online24x7, Double lastLat, Double lastLng) {}

    @PutMapping("/api/v1/masters/profile")
    @PreAuthorize("hasRole('MASTER')")
    public MasterProfile updateMyProfile(@RequestBody UpdateMasterReq req, Authentication auth) {
        UUID uid = UUID.fromString(auth.getName());
        MasterProfile mp = masters.findById(uid).orElseGet(() -> {
            MasterProfile created = new MasterProfile();
            created.setUserId(uid);
            return created;
        });
        mp.setBioUz(req.bioUz());
        mp.setBioRu(req.bioRu());
        mp.setBioEn(req.bioEn());
        mp.setExperienceYears(req.experienceYears());
        mp.setTashkentDistrict(req.tashkentDistrict());
        mp.setHourlyRateUzs(req.hourlyRateUzs());
        mp.setOnline24x7(req.online24x7());
        mp.setLastLat(req.lastLat());
        mp.setLastLng(req.lastLng());
        mp.setUpdatedAt(OffsetDateTime.now());
        return masters.save(mp);
    }

    @PatchMapping("/api/v1/admin/masters/{userId}/kyc")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN_S1')")
    public MasterProfile reviewKyc(@PathVariable UUID userId, @RequestBody Map<String, String> body) {
        MasterProfile mp = masters.findById(userId).orElseThrow();
        mp.setKycStatus(body.getOrDefault("kycStatus", "VERIFIED"));
        mp.setUpdatedAt(OffsetDateTime.now());
        return masters.save(mp);
    }
}
