package com.mholodniuk.searchthedocs.document;


import com.mholodniuk.searchthedocs.document.exception.DocumentParsingException;
import com.mholodniuk.searchthedocs.document.extract.impl.PdfExtractor;
import com.mholodniuk.searchthedocs.document.model.SearchableDocument;
import com.mholodniuk.searchthedocs.management.room.RoomService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// todo: rewrite and add tests
@SpringJUnitConfig(classes = {DocumentIndexService.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentIndexServiceTest {
    @MockBean
    private SearchService searchService;
    @MockBean
    private PdfExtractor contentExtractor;
    @MockBean
    private DocumentSearchRepository documentSearchRepository;
    @MockBean
    private RoomService roomService;

    private DocumentIndexService documentIndexService;

    @BeforeAll
    void setUp() {
        this.documentIndexService = new DocumentIndexService(
                searchService,
                Map.of("application/pdf", contentExtractor),
                documentSearchRepository,
                roomService);
    }

    @Test
    void Should_SaveAllPages_When_Indexing() throws IOException {
        var file = new MockMultipartFile("file", "sample1.pdf", "application/pdf", any(byte[].class));
        when(contentExtractor.extract(file.getBytes())).thenReturn(Collections.emptyList());

        var result = documentIndexService.indexDocument(file.getBytes(), "12-ew12-12dsa", file.getContentType(), file.getOriginalFilename(), null, null);

        verify(documentSearchRepository).saveAll(any());
        Assertions.assertEquals("12-ew12-12dsa", result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void Should_SaveTwoPages_When_IndexingFileWithTwoPages() throws IOException {
        var file = new MockMultipartFile("file", "sample2.pdf", "application/pdf", any(byte[].class));
        when(contentExtractor.extract(file.getBytes())).thenReturn(List.of(" A Simple PDF File ...", " A Simple PDF File 2 ..."));

        documentIndexService.indexDocument(file.getBytes(), "id", file.getContentType(), file.getOriginalFilename(), null, null);

        var documentsArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(documentSearchRepository).saveAll(documentsArgumentCaptor.capture());

        Assertions.assertEquals(2, getDocumentsSize(documentsArgumentCaptor.getValue()));
        Assertions.assertTrue(getFirstPage(documentsArgumentCaptor.getValue()).getText()
                .startsWith(" A Simple PDF File"));
    }

    @Test
    void Should_Throw_When_NullDocument() throws IOException {
        var file = new MockMultipartFile("file", "sample1.pdf", "application/pdf", any(byte[].class));
        when(contentExtractor.extract(file.getBytes())).thenThrow(DocumentParsingException.class);

        Assertions.assertThrows(DocumentParsingException.class, () -> documentIndexService.indexDocument(file.getBytes(), "id", file.getContentType(), file.getOriginalFilename(), null, null));
    }

    private long getDocumentsSize(Iterable<SearchableDocument> documents) {
        return StreamSupport.stream(documents.spliterator(), false).count();
    }

    private SearchableDocument getFirstPage(Iterable<SearchableDocument> documents) {
        return documents.iterator().next();
    }
}