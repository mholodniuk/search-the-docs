package com.mholodniuk.searchthedocs.file;

import com.mholodniuk.searchthedocs.document.DocumentService;
import com.mholodniuk.searchthedocs.file.dto.FileUploadResponse;
import com.mholodniuk.searchthedocs.file.exception.FileReadingException;
import com.mholodniuk.searchthedocs.file.exception.FileSavingException;
import com.mholodniuk.searchthedocs.file.mock.S3Mock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static com.mholodniuk.searchthedocs.file.mock.S3Mock.MOCK_BUCKET_NAME;

@Slf4j
@Service
@AllArgsConstructor
public class FileService {
    private final S3Mock s3;
    private final DocumentService documentService;

    public FileUploadResponse saveFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        try {
            final var bytes = file.getBytes();
            putObject(MOCK_BUCKET_NAME, filename, bytes);
            log.info("Saved file with name: {}", filename);
            var indexResult = documentService.indexDocument(bytes, file.getContentType(), filename);

            CompletableFuture.runAsync(() -> {
                var thumbnailBytes = ThumbnailGenerator.generateThumbnail(bytes);
                putObject(MOCK_BUCKET_NAME, filename + "-thumbnail", thumbnailBytes);
                log.info("Generated thumbnail for {}", filename);
            }).exceptionally((ex) -> {
                log.error("Error generating thumbnail for {}. Message: {}", filename, ex.getMessage());
                return null;
            });

            return new FileUploadResponse(filename, indexResult);
        } catch (IOException e) {
            log.error("Failed to save file with name: {}", filename);
            throw new FileSavingException(e);
        }
    }

    byte[] getFile(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> res = s3.getObject(getObjectRequest);

        try {
            log.debug("Returning file {}", key);
            return res.readAllBytes();
        } catch (IOException e) {
            log.error("Failed to read file from bucket {} with key: {}", bucketName, key);
            throw new FileReadingException(e);
        }
    }

    void putObject(String bucketName, String key, byte[] file) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.putObject(objectRequest, RequestBody.fromBytes(file));
    }
}
