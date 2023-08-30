package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
class DocumentManagementController {
    private final DocumentRepository documentRepository;

    @GetMapping
//    @PreAuthorize("@accessService.hasAccess(1, 1, authentication)")
    public List<Document> getAllDocuments() {
        return documentRepository.findAllWithAll();
    }

    @GetMapping("/{documentId}")
    public Document getById(@PathVariable String documentId) {
        return documentRepository.findById(UUID.fromString(documentId))
                .orElseThrow(() -> new ResourceNotFoundException("no document"));
    }

}
