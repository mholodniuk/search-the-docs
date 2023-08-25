package com.mholodniuk.searchmedaddy.document;


import com.mholodniuk.searchmedaddy.document.exception.DocumentParsingException;
import com.mholodniuk.searchmedaddy.document.extract.impl.PdfExtractor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {DocumentService.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentServiceTest {
    @MockBean
    private SearchService searchService;
    @MockBean
    private PdfExtractor contentExtractor;
    @MockBean
    private DocumentRepository documentRepository;

    private DocumentService documentService;

    @BeforeAll
    void setUp() {
        this.documentService = new DocumentService(
                searchService,
                Map.of("application/pdf", contentExtractor),
                documentRepository);
    }

    @Test
    @SneakyThrows
    void Should_SaveAllPages_When_Indexing() {
        var file = new MockMultipartFile("file", "sample1.pdf", "application/pdf", any(byte[].class));
        when(contentExtractor.extract(file.getBytes())).thenReturn(Collections.emptyList());

        var result = documentService.indexDocument(file.getBytes(), file.getContentType(), file.getOriginalFilename());

        verify(documentRepository).saveAll(any());
        Assertions.assertEquals("Created", result);
    }

    @Test
    @SneakyThrows
    void Should_SaveTwoPages_When_IndexingFileWithTwoPages() {
        var file = new MockMultipartFile("file", "sample2.pdf", "application/pdf", any(byte[].class));
        when(contentExtractor.extract(file.getBytes())).thenReturn(List.of(" A Simple PDF File ...", " A Simple PDF File 2 ..."));

        documentService.indexDocument(file.getBytes(), file.getContentType(), file.getOriginalFilename());

        var documentsArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(documentRepository).saveAll(documentsArgumentCaptor.capture());

        Assertions.assertEquals(2, getDocumentsSize(documentsArgumentCaptor.getValue()));
        Assertions.assertTrue(getFirstPage(documentsArgumentCaptor.getValue()).getText()
                .startsWith(" A Simple PDF File"));
    }

    @Test
    @SneakyThrows
    void Should_Throw_When_NullDocument() {
        var file = new MockMultipartFile("file", "sample1.pdf", "application/pdf", any(byte[].class));
        when(contentExtractor.extract(file.getBytes())).thenThrow(DocumentParsingException.class);

        Assertions.assertThrows(DocumentParsingException.class, () -> {
            documentService.indexDocument(file.getBytes(), file.getContentType(), file.getOriginalFilename());
        });
    }

    private long getDocumentsSize(Iterable<SearchableDocument> documents) {
        return StreamSupport.stream(documents.spliterator(), false).count();
    }

    private SearchableDocument getFirstPage(Iterable<SearchableDocument> documents) {
        return documents.iterator().next();
    }
}