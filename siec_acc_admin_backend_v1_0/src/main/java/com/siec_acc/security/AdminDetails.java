package com.siec_acc.security;

import com.siec_acc.entity.AdminEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AdminDetails implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(AdminDetails.class);
    private static final String DEFAULT_ROLE = "ADMIN";
    private final AdminEntity admin;

    public AdminDetails(AdminEntity admin) {
        if (admin == null) {
            throw new IllegalArgumentException("AdminEntity cannot be null");
        }
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = admin.getAdminRole();

        if (role == null || role.trim().isEmpty()) {
            role = DEFAULT_ROLE;
        }

        String authority = "ROLE_" + role.trim().toUpperCase();
        logger.debug("Granted authority: {} for user: {}", authority, admin.getAdminMobileNumber());

        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    public String getPassword() {
        return admin.getAdminPassword();
    }

    @Override
    public String getUsername() {
        return admin.getAdminMobileNumber();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        Boolean isLocked = admin.getAdminIsLocked();
        if (isLocked == null) return true;
        boolean nonLocked = !isLocked;
        if (!nonLocked) {
            logger.warn("Admin {} is locked", admin.getAdminMobileNumber());
        }
        return nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        Boolean isActive = admin.getAdminIsActive();
        if (isActive == null) return true;
        if (!isActive) {
            logger.warn("Admin {} is disabled", admin.getAdminMobileNumber());
        }
        return isActive;
    }

    public AdminEntity getAdminEntity() {
        return admin;
    }

    public String getAdminId() {
        return admin.getAdminId();
    }

    public String getMobileNumber() {
        return admin.getAdminMobileNumber();
    }

    public String getRole() {
        return admin.getAdminRole();
    }
}