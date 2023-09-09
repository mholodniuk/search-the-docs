package com.mholodniuk.searchthedocs.file;

import com.mholodniuk.searchthedocs.file.exception.FileSavingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static com.mholodniuk.searchthedocs.file.mock.S3Mock.MOCK_BUCKET_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final S3Service s3Service;

    public void saveFile(byte[] bytes, String documentId) {
        try {
            s3Service.putObject(MOCK_BUCKET_NAME, documentId, bytes);

            CompletableFuture.runAsync(() -> {
                log.warn("Thumbnails support only for PDFs");
                var thumbnailBytes = ThumbnailGenerator.generateThumbnail(bytes);
                s3Service.putObject(MOCK_BUCKET_NAME, documentId + "-thumbnail", thumbnailBytes);
                log.info("Generated thumbnail for {}", documentId);
            }).exceptionally((ex) -> {
                log.error("Error generating thumbnail for {}. Message: {}", documentId, ex.getMessage());
                return null;
            });
        } catch (Throwable e) {
            throw new FileSavingException(e);
        }
    }
}
