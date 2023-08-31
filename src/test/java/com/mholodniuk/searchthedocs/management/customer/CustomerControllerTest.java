package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.management.customer.dto.CreateCustomerRequest;
import com.mholodniuk.searchthedocs.management.customer.dto.CustomerResponse;
import com.mholodniuk.searchthedocs.management.customer.dto.UpdateCustomerRequest;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.RoomService;
import com.mholodniuk.searchthedocs.management.room.dto.RoomResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {
    @MockBean
    private CustomerService customerService;
    @MockBean
    private RoomService roomService;
    @Autowired
    private MockMvc mockMvc;


    @Test
    void Should_ReturnCustomerList_WhenFound() throws Exception {
        var customer1 = CustomerResponse.builder().id(1L).username("name1").build();
        var customer2 = CustomerResponse.builder().id(2L).username("name2").build();

        when(customerService.findAllCustomers()).thenReturn(List.of(customer1, customer2));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.customers.length()").value(2))
                .andExpect(jsonPath("$.customers[0].id").value("1"))
                .andExpect(jsonPath("$.customers[0].username").value("name1"))
                .andExpect(jsonPath("$.customers[1].id").value("2"))
                .andExpect(jsonPath("$.customers[1].username").value("name2"));
    }

    @Test
    void Should_ReturnEmptyCollection_WhenNothingFound() throws Exception {
        when(customerService.findAllCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.customers").isEmpty())
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    void Should_ReturnCustomer_When_Found() throws Exception {
        var customer = CustomerResponse.builder()
                .id(1L)
                .username("name")
                .displayName("display")
                .email("mail@mail.org")
                .token("token")
                .build();

        when(customerService.findCustomerById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(customer.id()))
                .andExpect(jsonPath("$.username").value(customer.username()))
                .andExpect(jsonPath("$.displayName").value(customer.displayName()))
                .andExpect(jsonPath("$.email").value(customer.email()));
    }

    @Test
    void Should_ReturnBadRequest_When_InvalidIdTypeProvided() throws Exception {
        var invalidId = "text";
        mockMvc.perform(get("/customers/%s".formatted(invalidId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.message").value("Incorrect argument format: '%s'".formatted(invalidId)));
    }

    @Test
    void Should_ReturnNotFound_When_NoCustomerFound() throws Exception {
        when(customerService.findCustomerById(4L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customers/4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void Should_ReturnRoomsOwnedByCustomer_When_SearchedByCustomerId() throws Exception {
        var room1 = RoomResponse.builder()
                .id(1L)
                .name("Default")
                .isPrivate(true)
                .createdAt(LocalDateTime.now().minusDays(2))
                .modifiedAt(LocalDateTime.now().minusDays(1))
                .build();

        var room2 = RoomResponse.builder()
                .id(2L)
                .name("Created")
                .isPrivate(false)
                .createdAt(LocalDateTime.now().minusDays(1))
                .modifiedAt(LocalDateTime.now().minusHours(3))
                .build();

        when(roomService.findRoomsByOwnerId(1L)).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/customers/1/rooms"))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.rooms.length()").value(2))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.rooms").isNotEmpty())
                .andExpect(jsonPath("$.rooms[0].id").value(1))
                .andExpect(jsonPath("$.rooms[1].id").value(2))
                .andExpect(jsonPath("$.rooms[0].name").value("Default"))
                .andExpect(jsonPath("$.rooms[1].isPrivate").value(false));
    }

    @Test
    void Should_CreateCustomer_When_ValidRequest() throws Exception {
        var customerCreatedResponse = CustomerResponse.builder()
                .username("name")
                .displayName("display")
                .id(1L)
                .token("token")
                .email("mail@mail.com")
                .build();

        when(customerService.createCustomer(any(CreateCustomerRequest.class)))
                .thenReturn(customerCreatedResponse);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "name",
                                    "displayName": "display",
                                    "email": "mail@mail.com",
                                    "password": "secret"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", Matchers.containsString("customers/1")));
    }

    @Test
    void Should_ReturnBadRequest_When_InvalidEmailFormat() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "name",
                                    "displayName": "display",
                                    "email": "invalid email format",
                                    "password": "secret"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].invalidValue").value("invalid email format"));
    }

    @Test
    void Should_ReturnBadRequest_When_InvalidEmailFormatAndLackingUsername() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "displayName": "display",
                                    "email": "invalid",
                                    "password": "secret"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors").isNotEmpty())
                // todo: somehow validate one object at a time
                .andExpect(jsonPath("$.errors[*].field", Matchers.containsInAnyOrder("username", "email")))
                .andExpect(jsonPath("$.errors[*].invalidValue", Matchers.containsInAnyOrder("invalid", null)));
    }

    @Test
    void Should_ReturnNotFound_When_EmptyRequest() throws Exception {
        mockMvc.perform(post("/customers"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Should_ReturnOkAndModifiedEntity_When_CorrectRequest() throws Exception {
        var updatedCustomer = CustomerResponse.builder()
                .id(3L)
                .username("to-be-changed")
                .displayName("display")
                .email("mail@mail.com")
                .build();
        when(customerService.updateCustomer(eq(3L), any(UpdateCustomerRequest.class)))
                .thenReturn(updatedCustomer);

        mockMvc.perform(put("/customers/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "to-be-changed"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(updatedCustomer.id()))
                .andExpect(jsonPath("$.username").value(updatedCustomer.username()))
                .andExpect(jsonPath("$.displayName").value(updatedCustomer.displayName()))
                .andExpect(jsonPath("$.email").value(updatedCustomer.email()));
    }

    @Test
    void Should_ReturnNotFound_When_NoToModifyCustomerFound() throws Exception {
        when(customerService.updateCustomer(eq(4L), any(UpdateCustomerRequest.class)))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/customers/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "to-be-changed",
                                    "displayName": "to-be-changed",
                                    "email": "to-be-changed@mail.com",
                                    "password": "to-be-changed"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void Should_ReturnConflict_When_ErrorsInModification() throws Exception {
        when(customerService.updateCustomer(eq(4L), any(UpdateCustomerRequest.class)))
                .thenThrow(InvalidResourceUpdateException.class);

        mockMvc.perform(put("/customers/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "to-be-changed",
                                    "email": "to-be-changed@mail.com"
                                }
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void Should_ReturnNotFound_When_NoCustomerWithId() throws Exception {
        doThrow(ResourceNotFoundException.class).when(customerService).deleteById(1L);
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void Should_ReturnNoContent_When_CustomerWithIdDeleted() throws Exception {
        mockMvc.perform(delete("/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void Should_ReturnBadRequest_When_InvalidId() throws Exception {
        mockMvc.perform(delete("/customers/aaa"))
                .andExpect(status().isBadRequest());
    }
}