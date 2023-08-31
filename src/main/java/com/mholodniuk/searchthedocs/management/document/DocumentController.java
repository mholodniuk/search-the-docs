package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.mholodniuk.searchthedocs.common.utils.CommonUtils.toUUID;

@Validated
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
class DocumentController {
    private final DocumentRepository documentRepository;

    @GetMapping
//    @PreAuthorize("@accessService.hasAccess(1, 1, authentication)")
    public List<Document> getAllDocuments() {
        return documentRepository.findAllWithAll();
    }

    @GetMapping("/{documentId}")
    public Document getById(@PathVariable @UUID String documentId) {
        return documentRepository.findById(toUUID(documentId))
                .orElseThrow(() -> new ResourceNotFoundException("no document"));
    }

}
