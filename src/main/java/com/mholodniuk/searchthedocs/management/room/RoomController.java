package com.mholodniuk.searchthedocs.management.room;

import com.mholodniuk.searchthedocs.management.access.AccessService;
import com.mholodniuk.searchthedocs.management.access.dto.GrantAccessRequest;
import com.mholodniuk.searchthedocs.management.document.DocumentService;
import com.mholodniuk.searchthedocs.management.dto.CollectionResponse;
import com.mholodniuk.searchthedocs.management.room.dto.CreateRoomRequest;
import com.mholodniuk.searchthedocs.management.room.dto.UpdateRoomRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
class RoomController {
    private final RoomService roomService;
    private final DocumentService documentService;
    private final AccessService accessService;

    @GetMapping
    public ResponseEntity<?> getRooms() {
        return ResponseEntity.ok(
                new CollectionResponse<>("rooms", roomService.findAllRooms())
        );
    }

    @GetMapping("/{roomId}")
    @PreAuthorize("@accessValidationService.validateRoomAnyAccess(authentication, #roomId)")
    public ResponseEntity<?> getRoom(@PathVariable Long roomId) {
        return roomService
                .findRoomById(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{roomId}/documents")
    @PreAuthorize("@accessValidationService.validateRoomAnyAccess(authentication, #roomId)")
    public ResponseEntity<?> getRoomDocuments(@PathVariable Long roomId) {
        return ResponseEntity.ok(
                new CollectionResponse<>("documents", documentService.findDocumentsInRoom(roomId))
        );
    }

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
    @PreAuthorize("@accessValidationService.validateRoomOwner(authentication, #roomId)")
    public ResponseEntity<?> updateRoom(@PathVariable Long roomId,
                                        @Valid @RequestBody UpdateRoomRequest updateRoomRequest) {
        var room = roomService.updateRoom(roomId, updateRoomRequest);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{roomId}")
    @PreAuthorize("@accessValidationService.validateRoomOwner(authentication, #roomId)")
    public ResponseEntity<?> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteById(roomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/access")
    @PreAuthorize("@accessValidationService.validateRoomFullAccess(authentication, #roomId)")
    public ResponseEntity<?> grantAccess(@PathVariable Long roomId,
                                         @Valid @RequestBody GrantAccessRequest grantAccessRequest) {
        var accessKey = accessService.grantAccess(roomId, grantAccessRequest);
        return ResponseEntity.created(
                linkTo(RoomController.class)
                        .slash(accessKey.room().id())
                        .toUri()
        ).body(accessKey);
    }

    @GetMapping("/{roomId}/access")
    @PreAuthorize("@accessValidationService.validateRoomFullAccess(authentication, #roomId)")
    public ResponseEntity<?> getRoomAccessKeys(@PathVariable Long roomId) {
        return ResponseEntity.ok(
                new CollectionResponse<>("keys", accessService.findRoomAccessKeys(roomId))
        );
    }

    @DeleteMapping("/{roomId}/access/{participantId}")
    @PreAuthorize("@accessValidationService.validateRoomFullAccess(authentication, #roomId)")
    public ResponseEntity<?> deleteAccessToRoom(@PathVariable Long roomId, @PathVariable Long participantId) {
        accessService.revokeAccess(roomId, participantId);
        return ResponseEntity.noContent().build();
    }
}
