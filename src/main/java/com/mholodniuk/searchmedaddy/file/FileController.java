package com.mholodniuk.searchmedaddy.file;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@AllArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        var fileResponse = fileService.saveFile(file, "mock");
        return ResponseEntity.created(
                linkTo(FileController.class)
                        .slash(fileResponse.key())
                        .toUri()
        ).build();
    }

    @GetMapping(value = "{file-id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getFile(@PathVariable("file-id") String imageId) {
        return fileService.getFile("mock", imageId);
    }
}
