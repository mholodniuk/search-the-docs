package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.management.access.AccessService;
import com.mholodniuk.searchthedocs.management.dto.CollectionResponse;
import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.UpdateCustomerRequest;
import com.mholodniuk.searchthedocs.management.room.RoomService;
import com.mholodniuk.searchthedocs.security.ApiAuthenticationService;
import com.mholodniuk.searchthedocs.security.dto.AuthenticationRequest;
import com.mholodniuk.searchthedocs.security.dto.AuthenticationResponse;
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
    private final AccessService accessService;
    private final ApiAuthenticationService authenticationService;

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
        return ResponseEntity.ok(
                new CollectionResponse<>("rooms", roomService.findRoomsByOwnerId(customerId))
        );
    }

    @GetMapping("/{customerId}/access")
    public ResponseEntity<?> getCustomerAccessKeys(@PathVariable Long customerId) {
        return ResponseEntity.ok(
                new CollectionResponse<>("keys", accessService.findCustomerAccessKeys(customerId))
        );
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

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return authenticationService.authenticate(request);
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