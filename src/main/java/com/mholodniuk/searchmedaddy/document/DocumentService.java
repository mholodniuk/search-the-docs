package com.mholodniuk.searchmedaddy.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.HighlighterType;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final ElasticsearchClient elasticsearchClient;
    private static final String DOCUMENT_INDEX = "documents";

    public Result indexDocument(MultipartFile file) throws IOException {
        var content = extractContent(file);

        for (int idx = 0; idx < content.size(); idx++) {
            var document = new Document(file.getOriginalFilename(), content.get(idx), idx);
            log.info("Indexing page {} of a file: {}", idx, document.name());
            elasticsearchClient.index(i -> i
                    .index(DOCUMENT_INDEX)
                    .document(document));
        }

        return Result.Created;
    }

    public void searchDocument(String phrase) {
        try {
            var response = elasticsearchClient.search(s -> s
                            .index(DOCUMENT_INDEX)
                            .source(SourceConfig.of(sc -> sc.filter(f -> f.excludes(List.of(FieldAttr.Document.TEXT_FIELD)))))
                            .highlight(Highlight.of(h -> h
                                    .fields(FieldAttr.Document.TEXT_FIELD, HighlightField.of(hf -> hf
                                            .fragmentSize(69)
                                            .preTags("<b>")
                                            .postTags("</b>")))
                                    .type(HighlighterType.Unified)))
                            .query(q -> q.match(t -> t.field(FieldAttr.Document.TEXT_FIELD).query(phrase))),
                    Document.class);

            response.hits().hits().forEach(hit -> {
                System.out.println("HIT: " + hit.highlight().get(FieldAttr.Document.TEXT_FIELD).toString().replace("\n", " "));
                System.out.println("FILE: " + Objects.requireNonNull(hit.source()).name());
            });

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private List<String> extractContent(MultipartFile multipartFile) {
        try (PDDocument document = PDDocument.load(multipartFile.getInputStream())) {
            // todo: beans?
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