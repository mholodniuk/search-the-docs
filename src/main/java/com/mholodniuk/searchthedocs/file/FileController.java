package com.mholodniuk.searchthedocs.file;

import com.mholodniuk.searchthedocs.file.validation.ValidFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:4200")
public class FileController {
    private final S3Service s3Service;
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam @ValidFile MultipartFile file,
                                        @RequestParam Long roomId,
                                        @RequestParam Long ownerId) {
        var fileUploadResponse = fileService.saveFile(file, roomId, ownerId);
        return ResponseEntity.created(
                linkTo(FileController.class)
                        .slash(fileUploadResponse.id())
                        .toUri()
        ).body(fileUploadResponse);
    }

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
