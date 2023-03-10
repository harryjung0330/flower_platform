package com.example.flowerplatform.security.authentication.userDetails;

import com.example.flowerplatform.repository.entity.AppUser.AppUser;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@EqualsAndHashCode
@ToString
public class AppUserDetails implements UserDetails
{
    private final Long userId;
    private final Role role;
    private final String email;
    private final String password;
    private final List<GrantedAuthority> grantedAuthorityList;

    public AppUserDetails(Long userId, Role role, String email, String password)
    {
        this.userId = userId;
        this.role = role;
        this.email = email;
        this.password = password;
        this.grantedAuthorityList = List.of( new SimpleGrantedAuthority(role.toString()));
    }

    public AppUserDetails(AppUser appUser)
    {
        this.userId = appUser.getId();
        this.role = appUser.getRole();
        this.email = appUser.getEmail();
        this.password = appUser.getPassword();
        this.grantedAuthorityList = List.of( new SimpleGrantedAuthority(role.toString()));
    }

    public Role getRole(){
        return this.role;
    }

    public Long getUserId()
    {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorityList;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
