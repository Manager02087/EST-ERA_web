package uz.estera.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "site_settings")
public class SiteSetting {
    @Id
    private String key;
    @Column(name = "value_uz", nullable = false)
    private String valueUz;
    @Column(name = "value_ru", nullable = false)
    private String valueRu;
    @Column(name = "value_en", nullable = false)
    private String valueEn;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValueUz() { return valueUz; }
    public void setValueUz(String valueUz) { this.valueUz = valueUz; }
    public String getValueRu() { return valueRu; }
    public void setValueRu(String valueRu) { this.valueRu = valueRu; }
    public String getValueEn() { return valueEn; }
    public void setValueEn(String valueEn) { this.valueEn = valueEn; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
