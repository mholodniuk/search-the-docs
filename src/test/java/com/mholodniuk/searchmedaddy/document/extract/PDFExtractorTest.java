package com.mholodniuk.searchmedaddy.document.extract;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {PDFExtractor.class})
class PDFExtractorTest {
    @Autowired
    private PDFExtractor extractor;

    @Value("classpath:sample1.pdf")
    Resource onePageFile;

    @Value("classpath:sample2.pdf")
    Resource twoPageFile;

    @Test
    @SneakyThrows
    void testExtractingSinglePageContent() {
        var expectedText = "Dummy PDF file";
        var expectedPagesNum = 1;
        var result = extractor.extract(onePageFile.getContentAsByteArray());

        Assertions.assertEquals(expectedPagesNum, result.size());
        Assertions.assertEquals(expectedText, result.get(0));
    }

    @Test
    @SneakyThrows
    void testExtractingTwoPageContent() {
        var expectedPagesNum = 2;
        var result = extractor.extract(twoPageFile.getContentAsByteArray());
        var firstPageContent = result.get(0);
        var secondPageContent = result.get(1);

        Assertions.assertEquals(expectedPagesNum, result.size());
        Assertions.assertTrue(firstPageContent.startsWith("A Simple PDF File"));
        Assertions.assertTrue(secondPageContent.startsWith("Simple PDF File 2"));
    }
}