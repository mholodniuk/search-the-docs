package com.mholodniuk.searchthedocs.security;

import com.mholodniuk.searchthedocs.management.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ApiUserDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByUsername(username)
                .map(customer -> new User(customer.getUsername(), customer.getPassword(), Collections.emptyList()))
                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
    }
}