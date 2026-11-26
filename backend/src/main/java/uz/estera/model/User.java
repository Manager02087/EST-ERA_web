package uz.estera.model;

import jakarta.persistence.*;
import uz.estera.security.UserRole;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CLIENT;
    @Column(name = "preferred_lang", nullable = false)
    private String preferredLang = "uz";
    @Column(name = "wallet_uzs", nullable = false)
    private long walletUzs = 0L;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UUID getId() { return id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getPreferredLang() { return preferredLang; }
    public void setPreferredLang(String preferredLang) { this.preferredLang = preferredLang; }
    public long getWalletUzs() { return walletUzs; }
    public void setWalletUzs(long walletUzs) { this.walletUzs = walletUzs; }
    public boolean isActive() { return active; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
