package com.mholodniuk.searchthedocs.file;

import com.mholodniuk.searchthedocs.file.exception.FileReadingException;
import com.mholodniuk.searchthedocs.file.mock.S3Mock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
class S3ServiceTest {
    @Mock
    private S3Mock s3Client;

    @InjectMocks
    private S3Service s3Service;

    @Test
    void Should_ProperlySaveAnObject_When_Called() throws IOException {
        String bucket = "bucket";
        String key = "id";
        byte[] data = "Hello".getBytes();

        s3Service.putObject(bucket, key, data);

        var putObjectRequestArgumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        var requestBodyArgumentCaptor = ArgumentCaptor.forClass(RequestBody.class);

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
    void Should_ProperlyReturnAnObject_When_Called() throws IOException {
        String bucket = "bucket";
        String key = "id";
        byte[] data = "Hello".getBytes();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();

        ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
        when(res.readAllBytes()).thenReturn(data);

        when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

        byte[] bytes = s3Service.getFile(bucket, key);

        Assertions.assertEquals(bytes, data);
    }

    @Test
    void Should_Throw_When_CannotReadBytes() throws IOException {
        String bucket = "bucket";
        String key = "id";

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();

        ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
        when(res.readAllBytes()).thenThrow(new IOException("Cannot read bytes"));

        when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

        Assertions.assertThrows(FileReadingException.class, () -> s3Service.getFile(bucket, key));
    }
}