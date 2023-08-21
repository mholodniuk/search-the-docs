package com.mholodniuk.searchmedaddy.document.extract;

import com.mholodniuk.searchmedaddy.document.exception.DocumentParsingException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Component("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
public class DocxExtractor implements ContentExtractor {
    @Override
    public List<String> extract(byte[] bytes) {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            var xwpfWordExtractor = new XWPFWordExtractor(document);
            var docText = xwpfWordExtractor.getText();

            return List.of(docText);
        } catch (IOException e) {
            throw new DocumentParsingException(e);
        }
    }
}
