package com.mholodniuk.searchthedocs.file.mock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class S3Mock implements S3Client {
    private static final String PATH = "/tmp/s3";
    private static final String SERVICE_NAME = "MOCK_S3_SERVICE";
    public static final String MOCK_BUCKET_NAME = "mock";

    @Override
    public String serviceName() {
        return SERVICE_NAME;
    }

    @Override
    public void close() {
    }

    @Override
    public PutObjectResponse putObject(PutObjectRequest putObjectRequest, RequestBody requestBody) throws AwsServiceException, SdkClientException {
        InputStream inputStream = requestBody.contentStreamProvider().newStream();

        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            FileUtils.writeByteArrayToFile(new File(buildObjectFullPath(putObjectRequest.bucket(), putObjectRequest.key())), bytes);
            return PutObjectResponse.builder().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DeleteObjectResponse deleteObject(DeleteObjectRequest deleteObjectRequest) {
        var path = buildObjectFullPath(deleteObjectRequest.bucket(), deleteObjectRequest.key());
        var reponseBuilder = DeleteObjectResponse.builder();
        try {
            Files.delete(Paths.get(path));
            return reponseBuilder.deleteMarker(true).build();
        } catch (IOException e) {
            return reponseBuilder.deleteMarker(false).build();
        }
    }

    @Override
    public ResponseInputStream<GetObjectResponse> getObject(GetObjectRequest getObjectRequest) throws AwsServiceException, SdkClientException {
        try {
            FileInputStream fileInputStream = new FileInputStream(buildObjectFullPath(getObjectRequest.bucket(), getObjectRequest.key()));
            return new ResponseInputStream<>(GetObjectResponse.builder().build(), fileInputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildObjectFullPath(String bucketName, String key) {
        return PATH + "/" + bucketName + "/" + key;
    }
}
