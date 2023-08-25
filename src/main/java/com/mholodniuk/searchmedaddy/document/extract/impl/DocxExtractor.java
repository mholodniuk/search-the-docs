package com.mholodniuk.searchmedaddy.document.extract.impl;

import com.mholodniuk.searchmedaddy.document.exception.DocumentParsingException;
import com.mholodniuk.searchmedaddy.document.extract.ContentExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
public class DocxExtractor implements ContentExtractor {
    @Override
    public List<String> extract(byte[] bytes) {
        try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            var xwpfWordExtractor = new XWPFWordExtractor(document);
            var docText = xwpfWordExtractor.getText().trim();

            // todo: split text into pages
            return List.of(docText);
        } catch (Exception e) {
            throw new DocumentParsingException(e);
        }
    }
}
