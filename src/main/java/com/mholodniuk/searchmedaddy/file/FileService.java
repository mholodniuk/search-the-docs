package com.mholodniuk.searchmedaddy.file;

import com.mholodniuk.searchmedaddy.document.DocumentService;
import com.mholodniuk.searchmedaddy.file.dto.FileUploadResponse;
import com.mholodniuk.searchmedaddy.file.exception.FileSavingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
class FileService {
    private final S3Client s3;
    private final DocumentService documentService;

    public FileUploadResponse saveFile(MultipartFile file, String bucketName) {
        String fileId = file.getOriginalFilename();
        try {
            putObject(bucketName, fileId, file.getBytes());
            log.info("Saved file with name: {}", fileId);
            var indexResult = documentService.indexDocument(file);
            return new FileUploadResponse(fileId, indexResult.toString());
        } catch (IOException e) {
            log.error("Failed to save file with name: {}", fileId);
            throw new FileSavingException(e);
        }
    }

    public byte[] getFile(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> res = s3.getObject(getObjectRequest);

        try {
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
