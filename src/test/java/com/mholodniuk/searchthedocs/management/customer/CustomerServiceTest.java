package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.CustomerResponse;
import com.mholodniuk.searchthedocs.management.customer.dto.UpdateCustomerRequest;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.folder.RoomService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RoomService roomService;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void Should_CreateAndReturnCustomer() {
        var request = new CreateCustomerRequest("username", "displayName", "email@email.com", "password");
        var entity = new Customer();
        entity.setId(1L);
        entity.setUsername(request.username());
        entity.setDisplayName(request.displayName());
        entity.setPassword(request.password());
        entity.setEmail(request.email());

        var response = customerService.createCustomer(request);

        verify(customerRepository, times(1)).save(any(Customer.class));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(request.username(), response.username());
        Assertions.assertEquals(request.displayName(), response.displayName());
        Assertions.assertEquals(request.email(), response.email());
        Assertions.assertTrue(response.createdAt().isBefore(LocalDateTime.now()));
        Assertions.assertNotNull(response.token());
    }

    @Test
    void Should_CreateDefaultRoom_When_CustomerGetsCreated() {
        var request = new CreateCustomerRequest(null, null, null, null);

        customerService.createCustomer(request);

        verify(roomService, times(1)).createDefaultRoom(any(Customer.class));
    }

    @Test
    void Should_ModifyAllFields_When_Requested() {
        var request = new UpdateCustomerRequest("username", "displayName", "test@mail.com", "password");
        var entity = new Customer();
        long customerId = 1L;
        entity.setId(1L);
        entity.setUsername("to-be-changed");
        entity.setDisplayName("to-be-changed");
        entity.setPassword("to-be-changed");
        entity.setEmail("to-be-changed");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(customerRepository.existsByUsername(request.username())).thenReturn(false);
        when(customerRepository.existsByEmail(request.email())).thenReturn(false);
        when(customerRepository.save(any())).thenReturn(entity);

        var response = customerService.updateCustomer(customerId, request);

        Assertions.assertEquals(request.username(), response.username());
        Assertions.assertEquals(request.email(), response.email());
        Assertions.assertEquals(request.displayName(), response.displayName());
    }

    @Test
    void Should_ModifyOnlyPresentFields_When_Requested() {
        var request = new UpdateCustomerRequest("username", null, null, "password");
        var entity = new Customer();
        long customerId = 1L;
        entity.setId(1L);
        entity.setUsername("to-be-changed");
        entity.setDisplayName("not-to-be-changed");
        entity.setPassword("to-be-changed");
        entity.setEmail("not-to-be-changed");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(customerRepository.existsByUsername(request.username())).thenReturn(false);
        when(customerRepository.save(any())).thenReturn(entity);

        var response = customerService.updateCustomer(customerId, request);

        Assertions.assertEquals(request.username(), response.username());
        Assertions.assertEquals("not-to-be-changed", response.email());
        Assertions.assertEquals("not-to-be-changed", response.displayName());
    }

    @Test
    void Should_Throw_When_NoCustomersFoundById() {
        var request = new UpdateCustomerRequest("username", null, null, "password");
        long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.updateCustomer(customerId, request));
    }

    @Test
    void Should_ThrowForInvalidField_When_EmailTaken() {
        var request = new UpdateCustomerRequest("username", null, "taken", "password");
        var entity = new Customer();
        long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(customerRepository.existsByUsername(request.username())).thenReturn(false);
        when(customerRepository.existsByEmail(request.email())).thenReturn(true);

        try {
            customerService.updateCustomer(customerId, request);
        } catch (RuntimeException e) {
            Assertions.assertInstanceOf(InvalidResourceUpdateException.class, e);
            var errors = ((InvalidResourceUpdateException) e).getErrors();
            Assertions.assertEquals(1, errors.size());
            Assertions.assertEquals(new ErrorMessage("email", "User with this email already exists", "taken"), errors.get(0));
        }
    }

    @Test
    void Should_ThrowForInvalidFields_When_EmailAndUsernameTaken() {
        var request = new UpdateCustomerRequest("taken", null, "also taken", "password");
        var entity = new Customer();
        long customerId = 1L;
        var expectedErrors = List.of(
                new ErrorMessage("email", "User with this email already exists", "also taken"),
                new ErrorMessage("username", "User with this username already exists", "taken"));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(entity));
        when(customerRepository.existsByUsername(request.username())).thenReturn(true);
        when(customerRepository.existsByEmail(request.email())).thenReturn(true);

        try {
            customerService.updateCustomer(customerId, request);
        } catch (RuntimeException e) {
            Assertions.assertInstanceOf(InvalidResourceUpdateException.class, e);
            var errors = ((InvalidResourceUpdateException) e).getErrors();
            Assertions.assertEquals(expectedErrors.size(), errors.size());
            Assertions.assertTrue(errors.containsAll(expectedErrors));
        }
    }

    @Test
    public void Should_DeleteCustomer_When_AskedFor() {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deleteById(1L);

        verify(customerRepository).delete(customer);
    }

    @Test
    public void Should_Throw_When_NoCustomerWithIdFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> customerService.deleteById(1L));
    }

    @Test
    public void Should_ReturnCorrectId_When_SearchedFor() {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<CustomerResponse> response = customerService.findCustomerById(1L);

        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(1L, response.get().id());
    }

    @Test
    public void Should_ReturnEmptyOptional_When_NoCustomerWithId() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<CustomerResponse> response = customerService.findCustomerById(1L);

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    public void Should_ReturnCorrectCustomers_When_SearchedForAll() {
        List<Customer> customers = new ArrayList<>();
        Customer customer1 = new Customer();
        customer1.setId(1L);
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customers.add(customer1);
        customers.add(customer2);

        when(customerRepository.findAll()).thenReturn(customers);

        List<CustomerResponse> responses = customerService.findAllCustomers();

        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals(1L, responses.get(0).id());
        Assertions.assertEquals(2L, responses.get(1).id());
    }
}