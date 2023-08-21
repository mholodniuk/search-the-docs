package com.mholodniuk.searchmedaddy.document;


import com.mholodniuk.searchmedaddy.document.extract.ContentExtractor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = {DocumentService.class})
class DocumentServiceTest {
    @MockBean
    private SearchService searchService;
    @MockBean
    private ContentExtractor contentExtractor;
    @MockBean
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentService documentService;

    @Test
    @SneakyThrows
    void Should_SaveAllPages_When_Indexing() {
        var file = new MockMultipartFile("file", "sample1.pdf", "application/pdf", any(byte[].class));

        var result = documentService.indexDocument(file.getBytes(), file.getOriginalFilename());

        verify(documentRepository).saveAll(any());
        Assertions.assertEquals("Created", result);
    }

    @Test
    @SneakyThrows
    void Should_SaveTwoPages_When_IndexingFileWithTwoPages() {
        var file = new MockMultipartFile("file", "sample2.pdf", "application/pdf", any(byte[].class));
        when(contentExtractor.extract(file.getBytes())).thenReturn(List.of(" A Simple PDF File ...", " A Simple PDF File 2 ..."));

        documentService.indexDocument(file.getBytes(), file.getOriginalFilename());

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