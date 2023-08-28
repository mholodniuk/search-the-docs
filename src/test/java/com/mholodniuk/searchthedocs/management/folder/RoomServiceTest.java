package com.mholodniuk.searchthedocs.management.folder;

import com.mholodniuk.searchthedocs.management.customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.mholodniuk.searchthedocs.management.folder.RoomConsts.DEFAULT_ROOM_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;
    @InjectMocks
    private RoomService roomService;


    @Test
    void Should_CreateDefaultRoomForCustomer_When_AskedFor() {
        var response = roomService.createDefaultRoom(new Customer());

        verify(roomRepository, times(1)).save(any(Room.class));
        Assertions.assertNotNull(response);
        Assertions.assertEquals(DEFAULT_ROOM_NAME, response.name());
        Assertions.assertTrue(response.createdAt().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(response.modifiedAt().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(response.isPrivate());
    }
}