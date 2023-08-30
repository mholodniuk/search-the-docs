package com.mholodniuk.searchthedocs.document;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<?> findByPhrase(@RequestParam("phrase") String phrase) {
        return documentService.searchDocument(phrase)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
