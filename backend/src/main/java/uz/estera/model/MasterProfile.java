package uz.estera.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "master_profiles")
public class MasterProfile {
    @Id
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "bio_uz") private String bioUz;
    @Column(name = "bio_ru") private String bioRu;
    @Column(name = "bio_en") private String bioEn;
    @Column(name = "experience_years", nullable = false) private short experienceYears = 1;
    @Column(name = "tashkent_district", nullable = false) private String tashkentDistrict = "Chilonzor";
    @Column(name = "rating_avg", nullable = false, precision = 3, scale = 2) private BigDecimal ratingAvg = new BigDecimal("5.00");
    @Column(name = "completed_orders", nullable = false) private int completedOrders = 0;
    @Column(name = "hourly_rate_uzs", nullable = false) private long hourlyRateUzs = 150000L;
    @Column(name = "kyc_status", nullable = false) private String kycStatus = "PENDING";
    @Column(name = "is_online_24x7", nullable = false) private boolean online24x7 = false;
    @Column(name = "last_lat") private Double lastLat;
    @Column(name = "last_lng") private Double lastLng;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt = OffsetDateTime.now();

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getBioUz() { return bioUz; }
    public void setBioUz(String bioUz) { this.bioUz = bioUz; }
    public String getBioRu() { return bioRu; }
    public void setBioRu(String bioRu) { this.bioRu = bioRu; }
    public String getBioEn() { return bioEn; }
    public void setBioEn(String bioEn) { this.bioEn = bioEn; }
    public short getExperienceYears() { return experienceYears; }
    public void setExperienceYears(short experienceYears) { this.experienceYears = experienceYears; }
    public String getTashkentDistrict() { return tashkentDistrict; }
    public void setTashkentDistrict(String tashkentDistrict) { this.tashkentDistrict = tashkentDistrict; }
    public BigDecimal getRatingAvg() { return ratingAvg; }
    public int getCompletedOrders() { return completedOrders; }
    public long getHourlyRateUzs() { return hourlyRateUzs; }
    public void setHourlyRateUzs(long hourlyRateUzs) { this.hourlyRateUzs = hourlyRateUzs; }
    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
    public boolean isOnline24x7() { return online24x7; }
    public void setOnline24x7(boolean online24x7) { this.online24x7 = online24x7; }
    public Double getLastLat() { return lastLat; }
    public void setLastLat(Double lastLat) { this.lastLat = lastLat; }
    public Double getLastLng() { return lastLng; }
    public void setLastLng(Double lastLng) { this.lastLng = lastLng; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
