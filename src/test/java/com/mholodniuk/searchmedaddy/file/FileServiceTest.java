package com.mholodniuk.searchmedaddy.file;

import com.mholodniuk.searchmedaddy.document.DocumentService;
import com.mholodniuk.searchmedaddy.file.exception.FileReadingException;
import com.mholodniuk.searchmedaddy.file.exception.FileSavingException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@SpringJUnitConfig(classes = {FileService.class})
class FileServiceTest {
    @MockBean
    private S3Mock s3Client;

    @MockBean
    private DocumentService documentService;

    @Autowired
    private FileService fileService;

    @Mock
    MultipartFile mockFile
            = new MockMultipartFile("name", "originalFileName", "contentType", new byte[]{0x00, 0x01});

    @Test
    void Should_ProperlySaveAnObject_When_Called() throws IOException {
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
    void Should_ProperlyReturnAnObject_When_Called() throws IOException {
        String bucket = "bucket";
        String key = "key";
        byte[] data = "Hello".getBytes();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();

        ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
        when(res.readAllBytes()).thenReturn(data);

        when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

        byte[] bytes = fileService.getFile(bucket, key);

        Assertions.assertEquals(bytes, data);
    }

    @Test
    void Should_Throw_When_CannotReadBytes() throws IOException {
        String bucket = "bucket";
        String key = "key";

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(key).build();

        ResponseInputStream<GetObjectResponse> res = mock(ResponseInputStream.class);
        when(res.readAllBytes()).thenThrow(new IOException("Cannot read bytes"));

        when(s3Client.getObject(eq(getObjectRequest))).thenReturn(res);

        Assertions.assertThrows(FileReadingException.class, () -> fileService.getFile(bucket, key));
    }

    @Test
    @SneakyThrows
    void Should_InvokeIndexing_When_FileSaved() {
        MultipartFile file = new MockMultipartFile("name", "originalFileName", "contentType", new byte[]{0x00, 0x01});
        given(documentService.indexDocument(file.getBytes(), "contentType", file.getOriginalFilename())).willReturn("Created");

        fileService.saveFile(file, "bucketName");

        then(documentService).should().indexDocument(file.getBytes(), "contentType", file.getOriginalFilename());
    }

    @Test
    @SneakyThrows
    void Should_ReturnProperIndexStatus_When_FileSaved() {
        MultipartFile file = new MockMultipartFile("name", "originalFileName", "contentType", new byte[]{0x00, 0x01});
        given(documentService.indexDocument(file.getBytes(), "contentType",  file.getOriginalFilename())).willReturn("Created");

        var uploadResponse = fileService.saveFile(file, "bucketName");

        Assertions.assertEquals(file.getOriginalFilename(), uploadResponse.key());
        Assertions.assertEquals("Created", uploadResponse.indexResult());
    }

    @Test
    @SneakyThrows
    void Should_Throw_When_FileInvalid() {
        given(mockFile.getBytes()).willThrow(IOException.class);

        Assertions.assertThrows(FileSavingException.class, () -> fileService.saveFile(mockFile, "bucket"));
    }
}