package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.management.dto.CollectionResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.mholodniuk.searchthedocs.common.utils.CommonUtils.toUUID;

@Validated
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
class DocumentController {
    private final DocumentService documentService;

    @GetMapping
//    @PreAuthorize("@accessService.hasAccess(1, 1, authentication)")
    public ResponseEntity<?> getAllDocuments() {
        return ResponseEntity.ok(
                new CollectionResponse<>("documents", documentService.findAllDocuments())
        );
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<?> getById(@PathVariable @UUID String documentId) {
        return documentService
                .findDocumentById(toUUID(documentId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
