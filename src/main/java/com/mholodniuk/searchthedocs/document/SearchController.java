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
            @RequestParam("requester") Long requesterId,
            @RequestParam(value = "fragment-size", defaultValue = "50") Integer fragmentSize) {
        return ResponseEntity.ok(documentIndexService.searchDocument(phrase, requesterId, fragmentSize));
    }
}
