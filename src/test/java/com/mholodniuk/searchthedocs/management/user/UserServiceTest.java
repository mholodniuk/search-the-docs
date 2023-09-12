package com.mholodniuk.searchthedocs.management.user;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.user.dto.CreateUserRequest;
import com.mholodniuk.searchthedocs.management.user.dto.UserDTO;
import com.mholodniuk.searchthedocs.management.user.dto.UserResponse;
import com.mholodniuk.searchthedocs.management.user.dto.UpdateUserRequest;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.Room;
import com.mholodniuk.searchthedocs.management.room.RoomService;
import com.mholodniuk.searchthedocs.security.ApiAuthenticationService;
import com.mholodniuk.searchthedocs.security.dto.AuthenticationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomService roomService;
    @Mock
    private ApiAuthenticationService authenticationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void Should_CreateAndReturnCustomer() {
        var request = new CreateUserRequest("username", "displayName", "email@email.com", "password");
        var entity = new User();
        entity.setId(1L);
        entity.setUsername(request.username());
        entity.setDisplayName(request.displayName());
        entity.setPassword(request.password());
        entity.setEmail(request.email());

        when(authenticationService.generateToken(request)).thenReturn(new AuthenticationResponse("token"));

        var response = userService.createUser(request);

        verify(userRepository, times(1)).save(any(User.class));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(request.username(), response.username());
        Assertions.assertEquals(request.displayName(), response.displayName());
        Assertions.assertEquals(request.email(), response.email());
        Assertions.assertNotNull(response.token());
    }

    @Test
    void Should_CreateDefaultRoom_When_CustomerGetsCreated() {
        var request = new CreateUserRequest(null, null, null, null);

        when(authenticationService.generateToken(request)).thenReturn(new AuthenticationResponse("token"));

        userService.createUser(request);

        verify(roomService, times(1)).createDefaultRoom(any(User.class));
    }

    @Test
    void Should_ModifyAllFields_When_Requested() {
        var request = new UpdateUserRequest("username", "displayName", "test@mail.com", "password");
        var customer = new User();
        long customerId = 1L;
        customer.setId(1L);
        customer.setUsername("to-be-changed");
        customer.setDisplayName("to-be-changed");
        customer.setPassword("to-be-changed");
        customer.setEmail("to-be-changed");

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(customer);

        userService.updateUser(customerId, request);

        var customerArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(customerArgumentCaptor.capture());
        var capturedSavedCustomer = customerArgumentCaptor.getValue();

        verify(userRepository, times(1)).save(any(User.class));
        Assertions.assertEquals(request.username(), capturedSavedCustomer.getUsername());
        Assertions.assertEquals(request.displayName(), capturedSavedCustomer.getDisplayName());
        Assertions.assertEquals(request.email(), capturedSavedCustomer.getEmail());
        Assertions.assertEquals(request.password(), capturedSavedCustomer.getPassword());
    }

    @Test
    void Should_ModifyOnlyPresentFields_When_Requested() {
        var request = new UpdateUserRequest("username", null, null, "password");
        var customer = new User();
        long customerId = 1L;
        customer.setId(1L);
        customer.setUsername("to-be-changed");
        customer.setDisplayName("not-to-be-changed");
        customer.setPassword("to-be-changed");
        customer.setEmail("not-to-be-changed");

        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(customer);

        userService.updateUser(customerId, request);

        var customerArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(customerArgumentCaptor.capture());
        var capturedSavedCustomer = customerArgumentCaptor.getValue();

        verify(userRepository, times(1)).save(any(User.class));
        Assertions.assertEquals(request.username(), capturedSavedCustomer.getUsername());
        Assertions.assertEquals(request.password(), capturedSavedCustomer.getPassword());
        Assertions.assertNotEquals(customer.getDisplayName(), request.displayName());
        Assertions.assertNotEquals(customer.getEmail(), request.email());
    }

    @Test
    void Should_Throw_When_NoCustomersFoundById() {
        var request = new UpdateUserRequest("username", null, null, "password");
        long customerId = 1L;

        when(userRepository.findById(customerId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(customerId, request));
    }

    @Test
    void Should_ThrowForInvalidField_When_EmailTaken() {
        var request = new UpdateUserRequest("username", null, "taken", "password");
        var entity = new User();
        long customerId = 1L;

        when(userRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        try {
            userService.updateUser(customerId, request);
        } catch (RuntimeException e) {
            Assertions.assertInstanceOf(InvalidResourceUpdateException.class, e);
            var errors = ((InvalidResourceUpdateException) e).getErrors();
            Assertions.assertEquals(1, errors.size());
            Assertions.assertEquals(new ErrorMessage("email", "User with this email already exists", "taken"), errors.get(0));
        }
    }

    @Test
    void Should_ThrowForInvalidFields_When_EmailAndUsernameTaken() {
        var request = new UpdateUserRequest("taken", null, "also taken", "password");
        var entity = new User();
        long customerId = 1L;
        var expectedErrors = List.of(
                new ErrorMessage("email", "User with this email already exists", "also taken"),
                new ErrorMessage("username", "User with this username already exists", "taken"));

        when(userRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(userRepository.existsByUsername(request.username())).thenReturn(true);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        try {
            userService.updateUser(customerId, request);
        } catch (RuntimeException e) {
            Assertions.assertInstanceOf(InvalidResourceUpdateException.class, e);
            var errors = ((InvalidResourceUpdateException) e).getErrors();
            Assertions.assertEquals(expectedErrors.size(), errors.size());
            Assertions.assertTrue(errors.containsAll(expectedErrors));
        }
    }

    @Test
    public void Should_DeleteCustomer_When_AskedFor() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteById(1L);

        verify(userRepository).delete(user);
    }

    @Test
    public void Should_Throw_When_NoCustomerWithIdFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.deleteById(1L));
    }

    @Test
    public void Should_ReturnCorrectId_When_SearchedFor() {
        var customer = new User();
        customer.setId(1L);
        var room = new Room();
        room.setId(1L);
        room.setPrivate(true);
        customer.setRooms(Set.of(room));

        when(userRepository.findByIdWithRooms(1L)).thenReturn(Optional.of(customer));

        Optional<UserResponse> response = userService.findUserById(1L);

        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(1L, response.get().id());
    }

    @Test
    public void Should_ReturnEmptyOptional_When_NoCustomerWithId() {
        when(userRepository.findByIdWithRooms(1L)).thenReturn(Optional.empty());

        Optional<UserResponse> response = userService.findUserById(1L);

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    public void Should_ReturnCorrectCustomers_When_SearchedForAll() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        users.add(user1);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> responses = userService.findAllUsers();

        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals(1L, responses.get(0).id());
        Assertions.assertEquals(2L, responses.get(1).id());
    }
}