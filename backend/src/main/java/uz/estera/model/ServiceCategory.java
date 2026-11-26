package uz.estera.model;

import jakarta.persistence.*;

@Entity
@Table(name = "service_categories")
public class ServiceCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String slug;
    @Column(name = "name_uz", nullable = false)
    private String nameUz;
    @Column(name = "name_ru", nullable = false)
    private String nameRu;
    @Column(name = "name_en", nullable = false)
    private String nameEn;
    @Column(name = "desc_uz", nullable = false)
    private String descUz;
    @Column(name = "desc_ru", nullable = false)
    private String descRu;
    @Column(name = "desc_en", nullable = false)
    private String descEn;
    @Column(name = "group_key", nullable = false)
    private String groupKey;
    @Column(name = "icon_key", nullable = false)
    private String iconKey;
    @Column(name = "base_price_uzs", nullable = false)
    private long basePriceUzs;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    @Column(name = "sort_order", nullable = false)
    private short sortOrder;

    public Integer getId() { return id; }
    public String getSlug() { return slug; }
    public String getNameUz() { return nameUz; }
    public String getNameRu() { return nameRu; }
    public String getNameEn() { return nameEn; }
    public String getDescUz() { return descUz; }
    public String getDescRu() { return descRu; }
    public String getDescEn() { return descEn; }
    public String getGroupKey() { return groupKey; }
    public String getIconKey() { return iconKey; }
    public long getBasePriceUzs() { return basePriceUzs; }
    public boolean isActive() { return active; }
    public short getSortOrder() { return sortOrder; }
}
