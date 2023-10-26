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
public class SearchController {
    private final DocumentIndexService documentIndexService;

    @GetMapping
    public ResponseEntity<?> findByPhrase(
            @RequestParam("phrase") String phrase,
            @RequestParam("requester") Long requesterId) {
        return documentIndexService.searchDocument(phrase, requesterId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
