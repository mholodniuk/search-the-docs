package com.mholodniuk.searchthedocs.management.user.mapper;

import com.mholodniuk.searchthedocs.management.user.User;
import com.mholodniuk.searchthedocs.management.user.dto.CreateUserRequest;
import com.mholodniuk.searchthedocs.management.user.dto.UserDto;
import com.mholodniuk.searchthedocs.management.user.dto.UserResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static User fromCreateRequest(CreateUserRequest createUserRequest) {
        var user = new User();
        user.setUsername(createUserRequest.username());
        user.setDisplayName(createUserRequest.displayName());
        user.setEmail(createUserRequest.email());
        return user;
    }

    public static UserDto toDTO(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .build();
    }

    public static UserDto toDTO(User user, String token) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .build();
    }
}
