package com.mholodniuk.searchmedaddy.file;

import com.mholodniuk.searchmedaddy.file.dto.FileUploadResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileUploadResponse uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.saveFile(file, "mock");
    }

    @GetMapping(value = "{file-id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] getFile(@PathVariable("file-id") String imageId) {
        return fileService.getFile("mock", imageId);
    }
}
