package com.mholodniuk.searchthedocs.security.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserEntity extends User {
    private final Long id;

    public UserEntity(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " [" +
                "ID=" + this.getId() + ", " +
                "Username=" + this.getUsername() + ", " +
                "Password=[PROTECTED]] ";
    }
}
