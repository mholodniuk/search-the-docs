package com.mholodniuk.searchmedaddy.file;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @Mock
    private S3Mock s3Client;

    private FileService fileService;

    @BeforeEach
    void setup() {
        fileService = new FileService(s3Client);
    }

    @Test
    void canPutObject() throws IOException {
        String bucket = "bucket";
        String key = "key";
        byte[] data = "Hello".getBytes();

        fileService.putObject(bucket, key, data);

        ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        ArgumentCaptor<RequestBody> requestBodyArgumentCaptor = ArgumentCaptor.forClass(RequestBody.class);

        verify(s3Client).putObject(putObjectRequestArgumentCaptor.capture(), requestBodyArgumentCaptor.capture());

        PutObjectRequest putObjectRequestArgumentCaptorValue = putObjectRequestArgumentCaptor.getValue();

        Assertions.assertEquals(putObjectRequestArgumentCaptorValue.bucket(), bucket);
        Assertions.assertEquals(putObjectRequestArgumentCaptorValue.key(), key);

        RequestBody requestBodyArgumentCaptorValue = requestBodyArgumentCaptor.getValue();

        Assertions.assertArrayEquals(
                requestBodyArgumentCaptorValue.contentStreamProvider().newStream().readAllBytes(),
                RequestBody.fromBytes(data).contentStreamProvider().newStream().readAllBytes()
        );
    }

    @Test
    void canGetObject() throws IOException {
        String bucket = "bucket";
        String key = "key";
        byte[] data = "Hello".getBytes();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();

        ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
        when(res.readAllBytes()).thenReturn(data);

        when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

        byte[] bytes = fileService.getObject(bucket, key);

        Assertions.assertEquals(bytes, data);
    }

    @Test
    void willThrowWhenGetObject() throws IOException {
        String bucket = "bucket";
        String key = "key";

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();

        ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
        when(res.readAllBytes()).thenThrow(new IOException("Cannot read bytes"));

        when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

        Assertions.assertThrows(FileReadingException.class, () -> fileService.getObject(bucket, key));
    }
}