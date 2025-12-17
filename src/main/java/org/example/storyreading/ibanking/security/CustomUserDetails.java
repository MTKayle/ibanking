package org.example.storyreading.ibanking.security;

import org.example.storyreading.ibanking.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private Long userId;
    private String phone;
    private String fullName;
    private String password;
    private User.Role role;
    private Boolean isLocked;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Long userId, String phone, String fullName, String password, User.Role role, Boolean isLocked) {
        this.userId = userId;
        this.phone = phone;
        this.fullName = fullName;
        this.password = password;
        this.role = role;
        this.isLocked = isLocked;
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role.name().toUpperCase())
        );
    }

    public static CustomUserDetails build(User user) {
        return new CustomUserDetails(
                user.getUserId(),
                user.getPhone(),
                user.getFullName(),
                user.getPasswordHash(),
                user.getRole(),
                user.getIsLocked()
        );
    }

    // Custom getters
    public Long getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getFullName() {
        return fullName;
    }

    public User.Role getRole() {
        return role;
    }

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return phone; // Use phone as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked; // Account is non-locked if isLocked is false
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
