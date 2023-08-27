package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.CustomerCreatedResponse;
import com.mholodniuk.searchthedocs.management.customer.mapper.CustomerMapper;
import com.mholodniuk.searchthedocs.management.folder.RoomService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final RoomService roomService;

    @Transactional
    public CustomerCreatedResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
        var customer = CustomerMapper.fromCreateRequest(createCustomerRequest);
        customerRepository.save(customer);
        roomService.createDefaultRoom(customer);

        return CustomerMapper.toCreatedResponse(customer);
    }

    @Deprecated
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
