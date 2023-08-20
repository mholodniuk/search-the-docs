package com.mholodniuk.searchmedaddy.document;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(classes = {DocumentService.class})
class DocumentServiceTest {
    @MockBean
    private SearchService searchService;
    @MockBean
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentService documentService;

    @Value("classpath:sample1.pdf")
    Resource onePageFile;

    @Value("classpath:sample2.pdf")
    Resource twoPageFile;

    @Test
    @SneakyThrows
    void indexingSinglePageDocumentTest() {
        var file = new MockMultipartFile("file", "sample1.pdf", "application/pdf", onePageFile.getContentAsByteArray());

        var result = documentService.indexDocument(file);

        verify(documentRepository).saveAll(any());
        Assertions.assertEquals("Created", result);
    }

    @Test
    @SneakyThrows
    void indexingTwoPageDocumentTest() {
        var file = new MockMultipartFile("file", "sample2.pdf", "application/pdf", twoPageFile.getContentAsByteArray());

        documentService.indexDocument(file);

        var documentsArgumentCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(documentRepository).saveAll(documentsArgumentCaptor.capture());

        Assertions.assertEquals(2, getDocumentsSize(documentsArgumentCaptor.getValue()));
        Assertions.assertTrue(getFirstPage(documentsArgumentCaptor.getValue()).getText().startsWith(" A Simple PDF File"));
    }

    private long getDocumentsSize(Iterable<Document> documents) {
        return StreamSupport.stream(documents.spliterator(), false).count();
    }

    private Document getFirstPage(Iterable<Document> documents) {
        return documents.iterator().next();
    }
}