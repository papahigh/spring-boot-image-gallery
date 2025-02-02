package io.github.papahigh.api;


import blobstore.BlobStore.BlobStoreException;
import io.github.papahigh.service.UploadService;
import io.github.papahigh.types.ImageDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pipeline.MediaType;
import pipeline.TempBlob;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;


    @PostMapping("/upload")
    public CompletableFuture<ImageDetails> upload(@RequestParam("file") MultipartFile file) throws IOException, BlobStoreException {
        var fileName = file.getOriginalFilename();
        var mediaType = MediaType.ofFileName(fileName);

        if (Objects.isNull(mediaType))
            throw new HttpBadRequestException("Media Type %s is not supported".formatted(file.getContentType()));

        var tempBlob = TempBlob.builder(fileName)
                .content(file.getInputStream())
                .mediaType(mediaType)
                .build();

        return uploadService.upload(tempBlob);
    }


    @ExceptionHandler(BlobStoreException.class)
    public ResponseEntity<?> handleBlobStoreException(BlobStoreException e) {
        log.error("Error occurred while serving fs blobstore", e);
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e) {
        log.error("Error occurred while serving fs blobstore", e);
        return ResponseEntity.internalServerError().build();
    }


    static class HttpBadRequestException extends ResponseStatusException {
        HttpBadRequestException(String reason) {
            super(HttpStatusCode.valueOf(400), reason);
        }
    }
}
