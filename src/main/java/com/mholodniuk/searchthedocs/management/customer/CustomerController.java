package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.CustomerCreatedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Validated
@RestController
@RequestMapping("/management/customers")
@RequiredArgsConstructor
class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public List<Customer> getAllUsers() {
        return customerService.getAllCustomers();
    }

    @PostMapping
    public ResponseEntity<CustomerCreatedResponse> registerCustomer(
            @Valid @RequestBody CreateCustomerRequest createcustomerRequest) {
        var customer = customerService.createCustomer(createcustomerRequest);
        return ResponseEntity.created(
                linkTo(CustomerController.class)
                        .slash(customer.id())
                        .toUri()
        ).body(customer);
    }
}