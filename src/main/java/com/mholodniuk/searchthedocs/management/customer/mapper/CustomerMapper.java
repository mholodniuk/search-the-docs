package com.mholodniuk.searchthedocs.management.customer.mapper;

import com.mholodniuk.searchthedocs.management.customer.Customer;
import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.CustomerDTO;
import com.mholodniuk.searchthedocs.management.customer.dto.CustomerResponse;
import com.mholodniuk.searchthedocs.management.room.mapper.RoomMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

    public static CustomerDTO toDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .displayName(customer.getDisplayName())
                .email(customer.getEmail())
                .token("jwt token TBA")
                .build();
    }

    public static CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .displayName(customer.getDisplayName())
                .email(customer.getEmail())
                .token("jwt token TBA")
                .rooms(RoomMapper.toDTO(customer.getRooms()))
                .build();
    }
}
