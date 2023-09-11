package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.customer.dto.*;
import com.mholodniuk.searchthedocs.management.customer.mapper.CustomerMapper;
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
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final RoomService roomService;
    private final ApiAuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CustomerDTO createCustomer(CreateCustomerRequest createCustomerRequest) {
        var customer = CustomerMapper.fromCreateRequest(createCustomerRequest);
        customer.setPassword(passwordEncoder.encode(createCustomerRequest.password()));
        customerRepository.save(customer);
        roomService.createDefaultRoom(customer);
        var authenticationResponse = authenticationService.generateToken(createCustomerRequest);

        return CustomerMapper.toDTO(customer, authenticationResponse.token());
    }

    public CustomerDTO updateCustomer(Long customerId, UpdateCustomerRequest updateRequest) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No customer with filename %s found".formatted(customerId)));

        var errors = new ArrayList<ErrorMessage>();
        applyIfChanged(customer.getUsername(), updateRequest.username(), (updated) -> {
            if (customerRepository.existsByUsername(updateRequest.username())) {
                errors.add(new ErrorMessage("username", "User with this username already exists", updateRequest.username()));
            } else {
                customer.setUsername(updated);
            }
        });
        applyIfChanged(customer.getPassword(), updateRequest.password(), customer::setPassword);
        applyIfChanged(customer.getDisplayName(), updateRequest.displayName(), customer::setDisplayName);
        applyIfChanged(customer.getEmail(), updateRequest.email(), (updated) -> {
            if (customerRepository.existsByEmail(updateRequest.email())) {
                errors.add(new ErrorMessage("email", "User with this email already exists", updateRequest.email()));
            } else {
                customer.setEmail(updated);
            }
        });

        if (!errors.isEmpty()) throw new InvalidResourceUpdateException("Cannot update entity", errors);

        var updated = customerRepository.save(customer);

        return CustomerMapper.toDTO(updated);
    }

    public void deleteById(Long customerId) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No customer with filename %s found".formatted(customerId)));
        customerRepository.delete(customer);
    }

    public Optional<CustomerResponse> findCustomerById(Long customerId) {
        return customerRepository.findByIdWithRooms(customerId).map(CustomerMapper::toResponse);
    }

    public List<CustomerDTO> findAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerMapper::toDTO)
                .toList();
    }
}
