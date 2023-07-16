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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final ElasticsearchClient elasticsearchClient;
    private static final String DOCUMENT_INDEX = "documents";

    public Result indexDocument(MultipartFile file, String id) throws IOException {
        var content = extractContent(file);
        var document = new Document(file.getOriginalFilename(), content);

        var docId = id != null ? id : UUID.randomUUID().toString();
        log.info("Trying to index a file with id: {} and name: {} was successfully indexed", docId, document.name());
        IndexResponse response = elasticsearchClient.index(i -> i
                .id(docId)
//                .pipeline()
                .index(DOCUMENT_INDEX)
                .document(document));

        return response.result();
    }

    public String extractContent(MultipartFile multipartFile) {
        try (PDDocument document = PDDocument.load(multipartFile.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (Exception e) {
            log.error("Error parsing PDF. Message: {}", e.getMessage());
            throw new DocumentParsingException(e);
        }
    }
}