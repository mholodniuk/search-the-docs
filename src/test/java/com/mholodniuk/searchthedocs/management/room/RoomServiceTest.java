package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.management.access.AccessService;
import com.mholodniuk.searchthedocs.management.user.User;
import com.mholodniuk.searchthedocs.management.user.UserRepository;
import com.mholodniuk.searchthedocs.management.document.Document;
import com.mholodniuk.searchthedocs.management.document.FileLocation;
import com.mholodniuk.searchthedocs.management.exception.InvalidResourceUpdateException;
import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import com.mholodniuk.searchthedocs.management.room.dto.CreateRoomRequest;
import com.mholodniuk.searchthedocs.management.room.dto.UpdateRoomRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static com.mholodniuk.searchthedocs.management.room.RoomConsts.DEFAULT_ROOM_NAME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccessService accessService;
    @InjectMocks
    private RoomService roomService;


    @Test
    void Should_CreateDefaultRoomForCustomer_When_AskedFor() {
        var response = roomService.createDefaultRoom(new User());

        verify(roomRepository, times(1)).save(any(Room.class));
        verify(accessService, times(1)).createSelfAccessKey(any(User.class), any(Room.class));
        Assertions.assertNotNull(response);
        Assertions.assertEquals(DEFAULT_ROOM_NAME, response.name());
        Assertions.assertTrue(response.createdAt().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(response.modifiedAt().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(response.isPrivate());
    }

    @Test
    void Should_Throw_When_RoomAlreadyExistsForCustomer() {
        var createRoomRequest = new CreateRoomRequest("room", false, 1L);

        when(roomRepository.existsByNameAndOwnerId("room", 1L)).thenReturn(true);

        try {
            roomService.createRoom(createRoomRequest);
        } catch (Exception e) {
            Assertions.assertInstanceOf(InvalidResourceUpdateException.class, e);
            var exception = (InvalidResourceUpdateException) e;
            Assertions.assertEquals("Cannot create entity", exception.getMessage());
            Assertions.assertEquals(1, exception.getErrors().size());
            var errorMessage = exception.getErrors().get(0).message();
            var errorField = exception.getErrors().get(0).field();
            Assertions.assertEquals("name", errorField);
            Assertions.assertEquals("User already owns room with given name", errorMessage);
        }
    }

    @Test
    void Should_Throw_When_CustomerDoesNotExists() {
        var createRoomRequest = new CreateRoomRequest("Room", true, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> roomService.createRoom(createRoomRequest));
    }

    @Test
    void Should_CreateRoom_When_RequestAndOthersValid() {
        var createRoomRequest = new CreateRoomRequest("valid room", true, 4L);
        var owner = new User();
        owner.setId(createRoomRequest.ownerId());

        when(userRepository.findById(4L)).thenReturn(Optional.of(owner));

        var createdRoom = roomService.createRoom(createRoomRequest);

        verify(roomRepository, times(1)).save(any(Room.class));
        verify(accessService, times(1)).createSelfAccessKey(any(User.class), any(Room.class));
        Assertions.assertEquals(createRoomRequest.name(), createdRoom.name());
        Assertions.assertEquals(createRoomRequest.isPrivate(), createdRoom.isPrivate());
        Assertions.assertTrue(createdRoom.createdAt().isBefore(LocalDateTime.now()));
        Assertions.assertTrue(createdRoom.modifiedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    void Should_ModifyAllFields_When_Requested() {
        var request = new UpdateRoomRequest("name", true);
        var room = new Room();
        room.setId(1L);
        room.setName("to-be-changed");
        room.setPrivate(false);
        room.setCreatedAt(LocalDateTime.now());
        var customer = new User();
        customer.setId(1L);
        room.setOwner(customer);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNameAndOwnerId(request.name(), customer.getId())).thenReturn(false);
        when(roomRepository.save(any())).thenReturn(room);

        var response = roomService.updateRoom(1L, request);

        var roomArgumentCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(roomArgumentCaptor.capture());
        var capturedSavedRoom = roomArgumentCaptor.getValue();

        verify(roomRepository, times(1)).save(any(Room.class));
        Assertions.assertEquals(request.name(), capturedSavedRoom.getName());
        Assertions.assertEquals(request.isPrivate(), capturedSavedRoom.isPrivate());
        Assertions.assertTrue(response.modifiedAt().isAfter(capturedSavedRoom.getCreatedAt()));
    }

    @Test
    void Should_ModifyOnlyPresentFields_When_Requested() {
        var request = new UpdateRoomRequest(null, false);
        var room = new Room();
        room.setId(1L);
        room.setName("to-be-changed");
        room.setPrivate(false);
        room.setCreatedAt(LocalDateTime.now());
        var customer = new User();
        customer.setId(1L);
        room.setOwner(customer);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(any())).thenReturn(room);

        var response = roomService.updateRoom(1L, request);

        var roomArgumentCaptor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(roomArgumentCaptor.capture());
        var capturedSavedRoom = roomArgumentCaptor.getValue();

        verify(roomRepository, times(1)).save(any(Room.class));
        Assertions.assertEquals(room.getName(), capturedSavedRoom.getName());
        Assertions.assertNotEquals(room.getName(), request.name());
        Assertions.assertEquals(request.isPrivate(), capturedSavedRoom.isPrivate());
        Assertions.assertTrue(response.modifiedAt().isAfter(response.createdAt()));
    }

    @Test
    void Should_Throw_When_NoRoomFoundById() {
        var request = new UpdateRoomRequest("room", true);

        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> roomService.updateRoom(1L, request));
    }

    @Test
    void Should_ThrowForInvalidField_When_RoomAlreadyExists() {
        var request = new UpdateRoomRequest("room", true);
        var room = new Room();
        var customer = new User();
        customer.setId(2L);
        room.setOwner(customer);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNameAndOwnerId(request.name(), 2L)).thenReturn(true);

        try {
            roomService.updateRoom(1L, request);
        } catch (Exception e) {
            Assertions.assertInstanceOf(InvalidResourceUpdateException.class, e);
            var errors = ((InvalidResourceUpdateException) e).getErrors();
            Assertions.assertEquals(1, errors.size());
            var error = errors.get(0);
            Assertions.assertEquals("name", error.field());
            Assertions.assertEquals("room", error.invalidValue());
        }
    }

    @Test
    public void Should_DeleteRoom_When_AskedFor() {
        var room = new Room();
        room.setId(1L);
        room.setName("room");
        room.setPrivate(false);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        roomService.deleteById(1L);

        verify(roomRepository, times(1)).delete(room);
    }

    @Test
    public void Should_Throw_When_NoRoomWithIdFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> roomService.deleteById(1L));
    }

    @Test
    public void Should_ReturnCorrectId_When_SearchedFor() {
        var room = new Room();
        room.setId(1L);
        room.setName("room");
        room.setPrivate(true);
        var document = new Document();
        document.setId(UUID.randomUUID());
        document.setFileLocation(new FileLocation());
        room.setDocuments(Set.of(document));

        when(roomRepository.findByIdWithDocuments(1L)).thenReturn(Optional.of(room));

        var response = roomService.findRoomById(1L);

        Assertions.assertTrue(response.isPresent());
        Assertions.assertEquals(1L, response.get().id());
    }

    @Test
    public void Should_ReturnEmptyOptional_When_NoRoomWithId() {
        when(roomRepository.findByIdWithDocuments(1L)).thenReturn(Optional.empty());

        var response = roomService.findRoomById(1L);

        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    public void Should_ReturnCorrectCustomers_When_SearchedForAll() {
        var room1 = new Room();
        room1.setId(1L);
        room1.setName("room");
        room1.setPrivate(true);
        var room2 = new Room();
        room2.setId(2L);
        room2.setName("room");
        room2.setPrivate(false);
        var rooms = new ArrayList<>(List.of(room1, room2));

        when(roomRepository.findAll()).thenReturn(rooms);

        var responses = roomService.findAllRooms();

        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals(1L, responses.get(0).id());
        Assertions.assertEquals(2L, responses.get(1).id());
    }
}