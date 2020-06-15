package com.bookstore.domain.security;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class Authority implements GrantedAuthority {

    private final String authority;

    @Override
    public String getAuthority() {
        return  authority;
    }
    
}