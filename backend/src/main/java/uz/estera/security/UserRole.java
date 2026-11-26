package uz.estera.security;

public enum UserRole {
    CLIENT,
    MASTER,
    SUPER_ADMIN,
    ADMIN_S1,
    ADMIN_S2,
    ADMIN_S3,
    SUPPORT_L1,
    SUPPORT_L2,
    SUPPORT_L3;

    public boolean isStaff() {
        return this != CLIENT && this != MASTER;
    }
}
