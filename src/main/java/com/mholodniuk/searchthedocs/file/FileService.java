package com.mholodniuk.searchthedocs.file;

import com.mholodniuk.searchthedocs.document.DocumentIndexService;
import com.mholodniuk.searchthedocs.file.dto.FileUploadResponse;
import com.mholodniuk.searchthedocs.file.exception.FileSavingException;
import com.mholodniuk.searchthedocs.management.document.DocumentService;
import com.mholodniuk.searchthedocs.management.document.dto.CreateDocumentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static com.mholodniuk.searchthedocs.file.mock.S3Mock.MOCK_BUCKET_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final DocumentIndexService documentIndexService;
    private final DocumentService documentService;
    private final S3Service s3Service;

    // todo: how to ensure "transaction"
    public FileUploadResponse saveFile(MultipartFile file, Long roomId, Long ownerId) {
        String filename = file.getOriginalFilename();
        try {
            final var bytes = file.getBytes();
            log.info("Saving file with name: {}", filename);
            var createDocumentRequest = CreateDocumentRequest.builder()
                    .name(filename)
                    .contentType(file.getContentType())
                    .filePath("/tmp/s3/" + MOCK_BUCKET_NAME + "/" + filename)
                    .storage("LOCAL")
                    .ownerId(ownerId)
                    .roomId(roomId)
                    .build();

            var createdDocument = documentService.createDocument(createDocumentRequest);
            var documentId = documentIndexService.indexDocument(bytes, createdDocument.id(), file.getContentType(), filename);
            s3Service.putObject(MOCK_BUCKET_NAME, createdDocument.id(), bytes);

            CompletableFuture.runAsync(() -> {
                log.warn("Thumbnails support only for PDFs");
                var thumbnailBytes = ThumbnailGenerator.generateThumbnail(bytes);
                s3Service.putObject(MOCK_BUCKET_NAME, createdDocument.id() + "-thumbnail", thumbnailBytes);
                log.info("Generated thumbnail for {}", filename);
            }).exceptionally((ex) -> {
                log.error("Error generating thumbnail for {}. Message: {}", filename, ex.getMessage());
                return null;
            });

            return new FileUploadResponse(documentId, filename);
        } catch (IOException e) {
            log.error("Failed to save file with name: {}", filename);
            throw new FileSavingException(e);
        }
    }
}
