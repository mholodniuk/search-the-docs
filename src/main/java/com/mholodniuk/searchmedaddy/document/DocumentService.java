package com.mholodniuk.searchmedaddy.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final ElasticsearchClient elasticsearchClient;
    private static final String DOCUMENT_INDEX = "documents";

    public Result indexDocument(MultipartFile file) throws IOException {
        var content = extractContent(file);

        var document = new Document(file.getOriginalFilename(), content);

        log.info("Indexing a file: {}", document.name());
        IndexResponse response = elasticsearchClient.index(i -> i
                .index(DOCUMENT_INDEX)
                .document(document));

        return response.result();
    }

    public void searchDocument(String phrase) {
        try {
            var response = elasticsearchClient.search(s -> s
                            .index(DOCUMENT_INDEX)
                            .query(q -> q.match(t -> t.field("content").query(phrase))),
                    Document.class);

            log.info("Doc: {}", response.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String extractContent(MultipartFile multipartFile) {
        try (PDDocument document = PDDocument.load(multipartFile.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (Exception e) {
            log.error("Error parsing PDF. Message: {}", e.getMessage());
            throw new DocumentParsingException(e);
        }
    }
}