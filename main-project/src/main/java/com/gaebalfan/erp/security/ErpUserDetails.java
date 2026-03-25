package com.gaebalfan.erp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ErpUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long   userId;
    private final String username;
    private final String password;
    private final String displayName;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Set<String> permissions;

    public ErpUserDetails(Long userId, String username, String password, String displayName,
                          List<? extends GrantedAuthority> authorities,
                          Set<String> permissions) {
        this.userId      = userId;
        this.username    = username;
        this.password    = password;
        this.displayName = displayName;
        this.authorities = authorities;
        this.permissions = permissions;
    }

    public boolean hasPermission(String page) {
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) return true;
        return permissions.contains(page);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }

    public Long getUserId() { return userId; }
    public String getDisplayName() { return displayName; }
}
