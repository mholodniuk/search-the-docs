package com.mholodniuk.searchthedocs.security.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class User extends org.springframework.security.core.userdetails.User {
    private final Long id;

    public User(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
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
