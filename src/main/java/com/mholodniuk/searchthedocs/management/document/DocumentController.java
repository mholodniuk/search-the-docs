package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.file.FileController;
import com.mholodniuk.searchthedocs.file.validation.ValidFile;
import com.mholodniuk.searchthedocs.management.document.dto.AssignTagsRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.mholodniuk.searchthedocs.common.utils.CommonUtils.toUUID;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Validated
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
class DocumentController {
    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@accessValidationService.validateRoomFullAccess(authentication, #roomId)")
    public ResponseEntity<?> uploadFile(@RequestParam @ValidFile MultipartFile file,
                                        @RequestParam Long roomId,
                                        @RequestParam Long ownerId) {
        var fileUploadResponse = documentService.uploadDocument(file, roomId, ownerId);
        return ResponseEntity.created(
                linkTo(FileController.class)
                        .slash(fileUploadResponse.id())
                        .toUri()
        ).body(fileUploadResponse);
    }

    @PatchMapping("/{documentId}")
    @PreAuthorize("@accessValidationService.validateDocumentAccess(authentication, #documentId)")
    public ResponseEntity<?> assignTags(
            @PathVariable @UUID String documentId,
            @RequestBody AssignTagsRequest assignTagsRequest) {
        var tags = documentService.assignTags(documentId, assignTagsRequest);
        return ResponseEntity.ok(tags);
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("@accessValidationService.validateDocumentAccess(authentication, #documentId)")
    public ResponseEntity<?> deleteRoom(@PathVariable @UUID String documentId) {
        documentService.deleteByDocumentId(documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{documentId}")
    @PreAuthorize("@accessValidationService.validateDocumentAccess(authentication, #documentId)")
    public ResponseEntity<?> getById(@PathVariable @UUID String documentId) {
        return documentService
                .findDocumentById(toUUID(documentId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
