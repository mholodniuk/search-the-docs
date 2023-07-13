package com.mholodniuk.searchmedaddy.file;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadImage(@RequestParam("file") MultipartFile file) {
        saveImage(file);
    }

    @GetMapping(value = "{image-id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getCustomerProfileImage(@PathVariable("image-id") String imageId) {
        return fileService.getObject("mock", imageId);
    }

    private void saveImage(MultipartFile file) {
        String imageId = UUID.randomUUID().toString();
        try {
            log.info("Saved image with id: {}", imageId);
            fileService.putObject("mock", imageId, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("failed to upload profile image", e);
        }
    }
}
