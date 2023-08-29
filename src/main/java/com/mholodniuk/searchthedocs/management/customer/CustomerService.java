package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.common.validation.ErrorMessage;
import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.CustomerResponse;
import com.mholodniuk.searchthedocs.management.customer.dto.UpdateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.mapper.CustomerMapper;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.folder.RoomService;
import com.mholodniuk.searchthedocs.management.folder.dto.RoomResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
        var customer = CustomerMapper.fromCreateRequest(createCustomerRequest);
        customerRepository.save(customer);
        roomService.createDefaultRoom(customer);

        return CustomerMapper.toResponse(customer);
    }

    public CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest updateRequest) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No customer with id %s found".formatted(customerId)));

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

        return CustomerMapper.toResponse(updated);
    }

    public void deleteCustomer(Long customerId) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No customer with id %s found".formatted(customerId)));
        customerRepository.delete(customer);
    }

    public Optional<CustomerResponse> getCustomerData(Long customerId) {
        return customerRepository.findById(customerId).map(CustomerMapper::toResponse);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(CustomerMapper::toResponse)
                .toList();
    }

    public List<RoomResponse> getCustomerRooms(Long customerId) {
        return roomService.findAllRoomsByOwnerId(customerId);
    }
}
