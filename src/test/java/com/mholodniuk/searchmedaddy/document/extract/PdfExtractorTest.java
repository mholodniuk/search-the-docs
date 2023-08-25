package com.mholodniuk.searchmedaddy.document.extract;

import com.mholodniuk.searchmedaddy.document.exception.DocumentParsingException;
import com.mholodniuk.searchmedaddy.document.extract.impl.PdfExtractor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {PdfExtractor.class})
class PdfExtractorTest {
    @Autowired
    private PdfExtractor extractor;

    @Value("classpath:pdf/sample1.pdf")
    Resource onePageFile;

    @Value("classpath:pdf/sample2.pdf")
    Resource twoPageFile;

    @Test
    @SneakyThrows
    void Should_ReturnOneElement_When_DocumentHasOnePage() {
        var expectedText = "Dummy PDF file";
        var expectedPagesNum = 1;
        var result = extractor.extract(onePageFile.getContentAsByteArray());

        Assertions.assertEquals(expectedPagesNum, result.size());
        Assertions.assertEquals(expectedText, result.get(0));
    }

    @Test
    @SneakyThrows
    void Should_ReturnTwoElements_When_DocumentHasTwoPages() {
        var expectedPagesNum = 2;
        var result = extractor.extract(twoPageFile.getContentAsByteArray());
        var firstPageContent = result.get(0);
        var secondPageContent = result.get(1);

        Assertions.assertEquals(expectedPagesNum, result.size());
        Assertions.assertTrue(firstPageContent.startsWith("A Simple PDF File"));
        Assertions.assertTrue(secondPageContent.startsWith("Simple PDF File 2"));
    }

    @Test
    @SneakyThrows
    void Should_ThrowException_When_FileInvalid() {
        Assertions.assertThrows(DocumentParsingException.class, () -> extractor.extract(new byte[]{1, 2, 3, 4}));
    }
}