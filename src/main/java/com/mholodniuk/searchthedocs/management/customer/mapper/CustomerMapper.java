package com.mholodniuk.searchthedocs.management.customer.mapper;

import com.mholodniuk.searchthedocs.management.customer.Customer;
import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.CustomerCreatedResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerMapper {
    public static Customer fromCreateRequest(CreateCustomerRequest createCustomerRequest) {
        var customer = new Customer();
        customer.setUsername(createCustomerRequest.username());
        customer.setDisplayName(createCustomerRequest.displayName());
        customer.setEmail(createCustomerRequest.email());
        customer.setPassword(createCustomerRequest.password());
        return customer;
    }

    public static CustomerCreatedResponse toCreatedResponse(Customer customer) {
        return CustomerCreatedResponse.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .displayName(customer.getDisplayName())
                .email(customer.getEmail())
                .createdAt(LocalDateTime.now())
                .token("jwt token TBA")
                .build();
    }
}
