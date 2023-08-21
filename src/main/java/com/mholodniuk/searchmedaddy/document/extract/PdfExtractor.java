package com.mholodniuk.searchmedaddy.document.extract;

import com.mholodniuk.searchmedaddy.document.exception.DocumentParsingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component("application/pdf")
public class PdfExtractor implements ContentExtractor {
    @Override
    public List<String> extract(byte[] bytes) {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(bytes))) {
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
            return pdfTextStripper.getText(document).trim();
        } catch (IOException e) {
            log.error("Error parsing page. Message: {}", e.getMessage());
            throw new DocumentParsingException(e);
        }
    }
}
