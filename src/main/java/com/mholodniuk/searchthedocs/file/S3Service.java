package com.mholodniuk.searchthedocs.file;

import com.mholodniuk.searchthedocs.file.exception.FileReadingException;
import com.mholodniuk.searchthedocs.file.mock.S3Mock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Slf4j
@Service
@AllArgsConstructor
public class S3Service {
    private final S3Mock s3;

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
            log.error("Failed to read file from bucket {} with id: {}", bucketName, key);
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
