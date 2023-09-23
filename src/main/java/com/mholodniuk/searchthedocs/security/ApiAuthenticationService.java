package com.mholodniuk.searchthedocs.security;

import com.mholodniuk.searchthedocs.management.user.dto.CreateUserRequest;
import com.mholodniuk.searchthedocs.security.dto.AuthenticationRequest;
import com.mholodniuk.searchthedocs.security.dto.AuthenticationResponse;
import com.mholodniuk.searchthedocs.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ApiAuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse generateToken(CreateUserRequest createUserRequest) {
        var user = buildUserDetails(createUserRequest.username(), createUserRequest.password());

        String jwtToken = jwtService.generateToken(user);
        return buildAuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest, String includeId) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password())
        );
        var user = buildUserDetails(authenticationRequest.username(), authenticationRequest.password());
        String jwtToken = jwtService.generateToken(user);

        if ("id".equals(includeId)) {
            return buildAuthenticationResponseWithId(auth, jwtToken);
        } else {
            return buildAuthenticationResponse(jwtToken);
        }
    }

    private static AuthenticationResponse buildAuthenticationResponse(String jwtToken) {
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private AuthenticationResponse buildAuthenticationResponseWithId(Authentication authentication, String jwtToken) {
        var principal = (com.mholodniuk.searchthedocs.security.model.User) authentication.getPrincipal();

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .id(principal.getId())
                .build();
    }

    private UserDetails buildUserDetails(String username, String password) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password)) // must be done again here
                .authorities(Collections.emptyList())
                .build();
    }
}
