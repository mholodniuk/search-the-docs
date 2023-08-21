package com.mholodniuk.searchmedaddy.file;

import com.mholodniuk.searchmedaddy.file.exception.ThumbnailGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
public class ThumbnailGenerator {
    public static byte[] generateThumbnail(byte[] fileBytes) {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileBytes))) {
            var renderer = new PDFRenderer(document);
            // todo: scale down image
            var bufferedImage = renderer.renderImageWithDPI(0, 50.0f, ImageType.RGB);
            var byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error generating thumbnail. Message: {}", e.getMessage());
            throw new ThumbnailGenerationException(e);
        }
    }
}
