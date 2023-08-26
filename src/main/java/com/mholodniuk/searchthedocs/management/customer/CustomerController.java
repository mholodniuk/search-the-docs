package com.mholodniuk.searchthedocs.management.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/management/customers")
@RequiredArgsConstructor
class CustomerController {
    private final CustomerRepository customerRepository;

    @GetMapping
    public List<Customer> getAllUsers() {
        return customerRepository.findAllWithRooms();
    }
}
