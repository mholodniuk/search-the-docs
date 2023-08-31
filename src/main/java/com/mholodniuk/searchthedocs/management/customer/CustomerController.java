package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.management.dto.CollectionResponse;
import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.UpdateCustomerRequest;
import com.mholodniuk.searchthedocs.management.room.RoomService;
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
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<?> getCustomers() {
        return ResponseEntity.ok(
                new CollectionResponse<>("customers", customerService.findAllCustomers())
        );
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomer(@PathVariable Long customerId) {
        return customerService
                .findCustomerById(customerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{customerId}/rooms")
    public ResponseEntity<?> getCustomerRooms(@PathVariable Long customerId) {
        return ResponseEntity.ok(roomService.findRoomsByOwnerId(customerId));
    }

    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CreateCustomerRequest createCustomerRequest) {
        var customer = customerService.createCustomer(createCustomerRequest);
        return ResponseEntity.created(
                linkTo(CustomerController.class)
                        .slash(customer.id())
                        .toUri()
        ).body(customer);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long customerId,
                                            @Valid @RequestBody UpdateCustomerRequest updateCustomerRequest) {
        var customer = customerService.updateCustomer(customerId, updateCustomerRequest);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteById(customerId);
        return ResponseEntity.noContent().build();
    }

}