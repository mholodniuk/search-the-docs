package com.mholodniuk.searchmedaddy.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;

@Slf4j
@Component
@Profile({"test", "local"})
public class S3Mock implements S3Client {
    private static final String PATH = "/tmp/s3";
    private static final String SERVICE_NAME = "MOCK_S3_SERVICE";

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
