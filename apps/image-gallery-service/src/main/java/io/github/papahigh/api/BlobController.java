package io.github.papahigh.api;

import blobstore.BlobPath;
import blobstore.BlobStore;
import blobstore.BlobStore.BlobStoreException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pipeline.MediaType;

import java.io.IOException;
import java.util.Objects;

import static io.github.papahigh.api.BlobController.Util.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;


@Slf4j
@RestController
@ConditionalOnProperty(value = "blobstore.fs.enabled")
public class BlobController {

    private final BlobStore blobStore;
    private final String cacheControl;

    public BlobController(BlobStore blobStore, @Value("${blobstore.fs.cache-control}") String cacheControl) {
        this.blobStore = blobStore;
        this.cacheControl = cacheControl;
    }

    @GetMapping("/uploads/**")
    public ResponseEntity<Resource> getInline(HttpServletRequest request) throws BlobStoreException {
        var blobPath = getBlobPath(request, "uploads");
        return ResponseEntity.ok()
                .header(CACHE_CONTROL, cacheControl)
                .header(CONTENT_TYPE, getContentType(blobPath))
                .header(CONTENT_DISPOSITION, getDisposition("inline", blobPath))
                .body(new InputStreamResource(blobStore.getBlob(blobPath)::inputStream));
    }

    @GetMapping("/download/**")
    public ResponseEntity<Resource> getAttachment(HttpServletRequest request) throws BlobStoreException {
        var blobPath = getBlobPath(request, "download");
        return ResponseEntity.ok()
                .header(CACHE_CONTROL, cacheControl)
                .header(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .header(CONTENT_DISPOSITION, getDisposition("attachment", blobPath))
                .body(new InputStreamResource(blobStore.getBlob(blobPath)::inputStream));
    }

    @ExceptionHandler(BlobStoreException.class)
    public ResponseEntity<?> handleBlobStoreException(BlobStoreException e) {
        log.error("Error occurred while serving fs blobstore", e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e) {
        log.error("Error occurred while serving fs blobstore", e);
        return ResponseEntity.notFound().build();
    }

    static class Util {

        static BlobPath getBlobPath(HttpServletRequest request, String mapping) {
            var uriPrefix = request.getContextPath() + mapping + "/";
            var imagePath = request.getRequestURI().substring(uriPrefix.length());
            return BlobPath.of(imagePath);
        }

        static String getDisposition(String context, BlobPath blobPath) {
            return context + "; filename=\"" + blobPath.lastPart() + "\"";
        }

        static String getContentType(BlobPath blobPath) {
            var mediaType = MediaType.ofFileName(blobPath.lastPart());
            Objects.requireNonNull(mediaType, "Media Type is not supported");
            return mediaType.typeName;
        }
    }
}
