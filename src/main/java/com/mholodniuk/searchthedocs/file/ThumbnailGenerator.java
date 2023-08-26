package com.mholodniuk.searchthedocs.file;

import com.mholodniuk.searchthedocs.file.exception.ThumbnailGenerationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ThumbnailGenerator {
    public static byte[] generateThumbnail(byte[] file) {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(file))) {
            var renderer = new PDFRenderer(document);
            var bufferedImage = renderer.renderImageWithDPI(0, 50.0f, ImageType.RGB);
            var byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new ThumbnailGenerationException(e);
        }
    }
}
