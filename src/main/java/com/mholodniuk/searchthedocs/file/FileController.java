package com.mholodniuk.searchthedocs.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {
    private final S3Service s3Service;

    @GetMapping(value = "/{bucket-name}/pdf/{file-id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getFile(@PathVariable("bucket-name") String bucketName,
                          @PathVariable("file-id") String fileId) {
        return s3Service.getFile(bucketName, fileId);
    }

    @GetMapping(value = "/{bucket-name}/{file-id}/thumbnail", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getFileThumbnail(@PathVariable("bucket-name") String bucketName,
                                   @PathVariable("file-id") String fileId) {
        return s3Service.getFile(bucketName, fileId + "-thumbnail");
    }
}
