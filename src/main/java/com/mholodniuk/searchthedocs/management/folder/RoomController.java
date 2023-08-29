package com.mholodniuk.searchthedocs.management.folder;

import com.mholodniuk.searchthedocs.management.folder.dto.CreateRoomRequest;
import com.mholodniuk.searchthedocs.management.folder.dto.RoomResponse;
import com.mholodniuk.searchthedocs.management.folder.dto.UpdateRoomRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
class RoomController {
    private final RoomService roomService;

    @GetMapping
    public List<RoomResponse> getRooms() {
        return roomService.findAllRooms();
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable Long roomId) {
        return roomService
                .findRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

//    @GetMapping("/{roomId}/documents")
//    public ResponseEntity<?> getRoomDocuments(@PathVariable Long roomId) {
//        return ResponseEntity.ok(documentRepository.findDocumentsByRoomId(customerId));
//    }

    @PostMapping
    public ResponseEntity<?> createRoom(@Valid @RequestBody CreateRoomRequest createRoomRequest) {
        var room = roomService.createRoom(createRoomRequest);
        return ResponseEntity.created(
                linkTo(RoomController.class)
                        .slash(room.id())
                        .toUri()
        ).body(room);
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId,
                                        @Valid @RequestBody UpdateRoomRequest updateRoomRequest) {
        var room = roomService.updateRoom(roomId, updateRoomRequest);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteById(roomId);
        return ResponseEntity.noContent().build();
    }
}
