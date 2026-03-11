package com.gaebalfan.erp.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class ErpUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Long   userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public ErpUserDetails(Long userId, String username, String password,
                          List<? extends GrantedAuthority> authorities) {
        this.userId      = userId;
        this.username    = username;
        this.password    = password;
        this.authorities = authorities;
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
}
