package com.mholodniuk.searchmedaddy.document.extract;

import com.mholodniuk.searchmedaddy.document.exception.DocumentParsingException;
import com.mholodniuk.searchmedaddy.document.extract.impl.DocxExtractor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {DocxExtractor.class})
public class DocxExtractorTest {
    @Autowired
    private DocxExtractor extractor;

    @Value("classpath:word/sample.docx")
    Resource onePageFile;

    @Test
    @SneakyThrows
    void Should_ParseSimpleWordDocument_When_AskedForIt() {
        var expectedText = "Dummy word file";
        var result = extractor.extract(onePageFile.getContentAsByteArray());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(expectedText, result.get(0));
    }

    @Test
    @SneakyThrows
    void Should_ThrowException_When_FileInvalid() {
        Assertions.assertThrows(DocumentParsingException.class, () -> extractor.extract(new byte[]{1, 2, 3, 4}));
    }
}
