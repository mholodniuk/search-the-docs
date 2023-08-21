package com.mholodniuk.searchmedaddy.file;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        var bucketName = "mock"; // todo: resolve bucket name where file should be stored
        var fileResponse = fileService.saveFile(file, bucketName);
        return ResponseEntity.created(
                linkTo(FileController.class)
                        .slash(fileResponse.key())
                        .toUri()
        ).build();
    }

    @GetMapping(value = "/{bucket-name}/{file-id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getFile(@PathVariable("bucket-name") String bucketName,
                          @PathVariable("file-id") String fileId) {
        return fileService.getFile(bucketName, fileId);
    }

    @GetMapping(value = "/{bucket-name}/{file-id}/thumbnail", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getFileThumbnail(@PathVariable("bucket-name") String bucketName,
                                   @PathVariable("file-id") String fileId) {
        return fileService.getFile(bucketName, fileId + "-thumbnail");
    }
}
