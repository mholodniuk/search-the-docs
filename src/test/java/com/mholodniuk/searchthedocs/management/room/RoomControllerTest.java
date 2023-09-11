package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.management.access.AccessService;
import com.mholodniuk.searchthedocs.management.document.DocumentService;
import com.mholodniuk.searchthedocs.management.room.dto.RoomDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RoomControllerTest {
    @MockBean
    private RoomService roomService;
    @MockBean
    private DocumentService documentService;
    @MockBean
    private AccessService accessService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void Should_ReturnRoomList_WhenFound() throws Exception {
        var room1 = RoomDTO.builder().id(1L).name("name1").build();
        var room2 = RoomDTO.builder().id(2L).name("name2").build();

        when(roomService.findAllRooms()).thenReturn(List.of(room1, room2));

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.rooms.length()").value(2))
                .andExpect(jsonPath("$.rooms[0].id").value("1"))
                .andExpect(jsonPath("$.rooms[0].name").value("name1"))
                .andExpect(jsonPath("$.rooms[1].id").value("2"))
                .andExpect(jsonPath("$.rooms[1].name").value("name2"));
    }

    @Test
    void Should_ReturnEmptyCollection_WhenNothingFound() throws Exception {
        when(roomService.findAllRooms()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.rooms").isEmpty())
                .andExpect(jsonPath("$.count").value(0));
    }
}
