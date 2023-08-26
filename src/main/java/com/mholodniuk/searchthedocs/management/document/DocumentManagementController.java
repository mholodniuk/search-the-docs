package com.mholodniuk.searchthedocs.management.document;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("management/documents")
@RequiredArgsConstructor
class DocumentManagementController {
    private final DocumentRepository documentRepository;

    @GetMapping
//    @PreAuthorize("@accessService.hasAccess(1, 1, authentication)")
    public List<Document> getAllDocuments() {
        return documentRepository.findAllWithAll();
    }

}
