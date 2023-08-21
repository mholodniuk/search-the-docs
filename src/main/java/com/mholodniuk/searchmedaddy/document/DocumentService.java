package com.mholodniuk.searchmedaddy.document;

import com.mholodniuk.searchmedaddy.document.dto.PhraseSearchResponse;
import com.mholodniuk.searchmedaddy.document.exception.DocumentParsingException;
import com.mholodniuk.searchmedaddy.document.mapper.SearchResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final SearchService searchService;
    private final DocumentRepository documentRepository;

    public String indexDocument(byte[] file, String filename) {
        var content = extractContent(file);

        var documents = IntStream.range(0, content.size())
                .mapToObj(pageIdx -> new Document(filename, content.get(pageIdx), pageIdx + 1))
                .peek(document -> log.debug("Indexing page {}. with content: {}", document.getPage(), document.getText()))
                .toList();

        documentRepository.saveAll(documents);
        log.info("Indexed {} page(s) of a file: {}", documents.size(), filename);

        return "Created";
    }

    public Optional<PhraseSearchResponse> searchDocument(String phrase) {
        try {
            var response = searchService.searchDocumentsByPhrase(phrase);
            var searchResponse = SearchResponseMapper.mapToDto(response);

            return Stream.of(searchResponse)
                    .filter(result -> result.hits().size() > 0)
                    .findAny();
        } catch (IOException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    private List<String> extractContent(byte[] fileBytes) {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileBytes))) {
            var pdfStripper = new PDFTextStripper();
            var splitter = new Splitter();
            var pages = splitter.split(document);

            return pages.stream().map(page -> this.parsePage(pdfStripper, page)).toList();
        } catch (Exception e) {
            log.error("Error loading PDF. Message: {}", e.getMessage());
            throw new DocumentParsingException(e);
        }
    }

    private String parsePage(PDFTextStripper pdfTextStripper, PDDocument document) {
        try {
            return pdfTextStripper.getText(document);
        } catch (IOException e) {
            log.error("Error parsing page. Message: {}", e.getMessage());
            throw new DocumentParsingException(e);
        }
    }
}