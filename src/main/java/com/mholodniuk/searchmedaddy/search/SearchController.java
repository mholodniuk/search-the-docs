package com.mholodniuk.searchmedaddy.search;

import com.mholodniuk.searchmedaddy.document.DocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final DocumentService documentService;

    @GetMapping("/parse")
    public void indexDocuments() throws IOException {
        List<String> documents = List.of(
                "/Users/maciejholodniuk/Downloads/dummy.pdf",
                "/Users/maciejholodniuk/Downloads/cv.pdf",
                "/Users/maciejholodniuk/Downloads/Holodniuk_Maciej_CV.pdf"
        );

        for (String doc : documents) {
            log.info(documentService.indexDocument(doc));
        }
    }
}
