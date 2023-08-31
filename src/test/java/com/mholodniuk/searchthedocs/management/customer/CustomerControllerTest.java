package com.mholodniuk.searchthedocs.management.customer;

import com.mholodniuk.searchthedocs.management.customer.dto.CustomerResponse;
import com.mholodniuk.searchthedocs.management.room.RoomService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @SneakyThrows
    void Should_ReturnCustomerList_WhenFound() {
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
    @SneakyThrows
    void Should_ReturnEmptyCollection_WhenNothingFound() {
        when(customerService.findAllCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.customers").isEmpty())
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @SneakyThrows
    void Should_ReturnCustomer_When_Found() {
        var customer = CustomerResponse.builder()
                .id(1L)
                .username("name")
                .displayName("display")
                .email("mail@mail.org")
                .createdAt(LocalDateTime.now().minusDays(1))
                .token("token")
                .build();
        when(customerService.findCustomerById(1L)).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(customer.id()))
                .andExpect(jsonPath("$.username").value(customer.username()))
                .andExpect(jsonPath("$.displayName").value(customer.displayName()))
                .andExpect(jsonPath("$.email").value(customer.email()))
                .andExpect(jsonPath("$.createdAt").value(customer.createdAt().toString()));
    }

    @Test
    @SneakyThrows
    void Should_ReturnBadRequest_When_InvalidIdTypeProvided() {
        var invalidId = "text";
        mockMvc.perform(get("/customers/%s".formatted(invalidId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.message").value("Incorrect argument format: '%s'".formatted(invalidId)));
    }

    @Test
    @SneakyThrows
    void Should_ReturnNotFound_When_NoCustomerFound() {
        when(customerService.findCustomerById(4L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customers/4")).andExpect(status().isNotFound());
    }
//
//    @Test
//    void getCustomerRooms() {
//    }
//
//    @Test
//    void createCustomer() {
//    }
//
//    @Test
//    void updateCustomer() {
//    }
//
//    @Test
//    void deleteCustomer() {
//    }
}