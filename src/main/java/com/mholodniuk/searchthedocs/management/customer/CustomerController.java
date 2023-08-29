package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.UpdateCustomerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Validated
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
class CustomerController {
    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<?> getCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerData(@PathVariable Long customerId) {
        return customerService
                .getCustomerData(customerId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{customerId}/rooms")
    public ResponseEntity<?> getCustomerRooms(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerRooms(customerId));
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(
            @Valid @RequestBody CreateCustomerRequest createcustomerRequest) {
        var customer = customerService.createCustomer(createcustomerRequest);
        return ResponseEntity.created(
                linkTo(CustomerController.class)
                        .slash(customer.id())
                        .toUri()
        ).body(customer);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody UpdateCustomerRequest updateCustomerRequest) {
        var customer = customerService.updateCustomer(customerId, updateCustomerRequest);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

}