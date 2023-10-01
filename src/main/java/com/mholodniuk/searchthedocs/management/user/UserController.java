package com.mholodniuk.searchthedocs.management.user;

import com.mholodniuk.searchthedocs.management.access.AccessService;
import com.mholodniuk.searchthedocs.management.dto.CollectionResponse;
import com.mholodniuk.searchthedocs.management.user.dto.CreateUserRequest;
import com.mholodniuk.searchthedocs.management.user.dto.UpdateUserRequest;
import com.mholodniuk.searchthedocs.management.room.RoomService;
import com.mholodniuk.searchthedocs.security.ApiAuthenticationService;
import com.mholodniuk.searchthedocs.security.dto.AuthenticationRequest;
import com.mholodniuk.searchthedocs.security.dto.AuthenticationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;
    private final RoomService roomService;
    private final AccessService accessService;
    private final ApiAuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(
                new CollectionResponse<>("users", userService.findAllUsers())
        );
    }

    @GetMapping("/{userId}")
    @PreAuthorize("@accessValidationService.validateUserAccess(authentication, #userId)")
    public ResponseEntity<?> getUser(@PathVariable Long userId) {
        return userService
                .findUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/rooms")
    @PreAuthorize("@accessValidationService.validateUserAccess(authentication, #userId)")
    public ResponseEntity<?> getUserRooms(@PathVariable Long userId) {
        return ResponseEntity.ok(
                new CollectionResponse<>("rooms", roomService.findAvailableRooms(userId))
        );
    }

    @GetMapping("/{userId}/access")
    @PreAuthorize("@accessValidationService.validateUserAccess(authentication, #userId)")
    public ResponseEntity<?> getUserAccessKeys(@PathVariable Long userId) {
        return ResponseEntity.ok(
                new CollectionResponse<>("keys", accessService.findAllUserAccessKeys(userId))
        );
    }

    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        var user = userService.createUser(createUserRequest);
        return ResponseEntity.created(
                linkTo(UserController.class)
                        .slash(user.id())
                        .toUri()
        ).body(user);
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticateUser(
            @Valid @RequestBody AuthenticationRequest request,
            @RequestParam(value = "include", required = false, defaultValue = "") String includeId) {
        return authenticationService.authenticate(request, includeId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("@accessValidationService.validateUserAccess(authentication, #userId)")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        var user = userService.updateUser(userId, updateUserRequest);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("@accessValidationService.validateUserAccess(authentication, #userId)")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

}