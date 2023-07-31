package com.mholodniuk.searchmedaddy.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.HighlighterType;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import com.mholodniuk.searchmedaddy.document.dto.PhraseSearchResponse;
import com.mholodniuk.searchmedaddy.document.exception.DocumentParsingException;
import com.mholodniuk.searchmedaddy.document.mapper.SearchResponseMapper;
import com.mholodniuk.searchmedaddy.document.utils.FieldAttr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final ElasticsearchClient elasticsearchClient;
    private final DocumentRepository documentRepository;

    public Result indexDocument(MultipartFile file) {
        var content = extractContent(file);

        var documents = IntStream.range(0, content.size())
                .mapToObj(pageIdx -> new Document(file.getOriginalFilename(), content.get(pageIdx), pageIdx))
                .toList();

        documentRepository.saveAll(documents);
        log.info("Indexed {} page(s) of a file: {}", documents.size(), file.getOriginalFilename());

        return Result.Created;
    }

    public Optional<PhraseSearchResponse> searchDocument(String phrase) {
        try {
            var response = searchDocumentsByPhrase(phrase);
            var searchResponse = SearchResponseMapper.mapToDto(response);

            return searchResponse.hits().size() > 0 ? Optional.of(searchResponse) : Optional.empty();

        } catch (IOException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    private SearchResponse<Document> searchDocumentsByPhrase(String phrase) throws IOException {
        return elasticsearchClient.search(s -> s
                        .index(Document.getIndexName())
                        .source(SourceConfig.of(sc -> sc.filter(f -> f.excludes(List.of(FieldAttr.Document.TEXT_FIELD, "_class")))))
                        .highlight(Highlight.of(h -> h
                                .fields(FieldAttr.Document.TEXT_FIELD, HighlightField.of(hf -> hf
                                        .fragmentSize(69)
                                        .preTags("<b>")
                                        .postTags("</b>")))
                                .type(HighlighterType.Unified)))
                        .query(q -> q.match(t -> t.field(FieldAttr.Document.TEXT_FIELD).query(phrase))),
                Document.class);
    }

    private List<String> extractContent(MultipartFile multipartFile) {
        try (PDDocument document = PDDocument.load(multipartFile.getInputStream())) {
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