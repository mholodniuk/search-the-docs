package com.mholodniuk.searchthedocs.management.user;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.user.dto.*;
import com.mholodniuk.searchthedocs.management.user.mapper.UserMapper;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.RoomService;
import com.mholodniuk.searchthedocs.security.ApiAuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mholodniuk.searchthedocs.common.operation.Operation.applyIfChanged;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoomService roomService;
    private final ApiAuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(CreateUserRequest createUserRequest) {
        var user = UserMapper.fromCreateRequest(createUserRequest);
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        userRepository.save(user);
        roomService.createDefaultRoom(user);
        var authenticationResponse = authenticationService.generateToken(createUserRequest);

        return UserMapper.toDTO(user, authenticationResponse.token());
    }

    public UserDto updateUser(Long userId, UpdateUserRequest updateRequest) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No user with filename %s found".formatted(userId)));

        var errors = new ArrayList<ErrorMessage>();
        applyIfChanged(user.getUsername(), updateRequest.username(), (updated) -> {
            if (userRepository.existsByUsername(updateRequest.username())) {
                errors.add(new ErrorMessage("username", "User with this username already exists", updateRequest.username()));
            } else {
                user.setUsername(updated);
            }
        });
        applyIfChanged(user.getPassword(), updateRequest.password(), user::setPassword);
        applyIfChanged(user.getDisplayName(), updateRequest.displayName(), user::setDisplayName);
        applyIfChanged(user.getEmail(), updateRequest.email(), (updated) -> {
            if (userRepository.existsByEmail(updateRequest.email())) {
                errors.add(new ErrorMessage("email", "User with this email already exists", updateRequest.email()));
            } else {
                user.setEmail(updated);
            }
        });

        if (!errors.isEmpty()) throw new InvalidResourceUpdateException("Cannot update entity", errors);

        var updated = userRepository.save(user);

        return UserMapper.toDTO(updated);
    }

    public void deleteById(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No user with filename %s found".formatted(userId)));
        userRepository.delete(user);
    }

    public Optional<UserResponse> findUserById(Long userId) {
        return userRepository.findByIdWithRooms(userId).map(UserMapper::toResponse);
    }

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}
