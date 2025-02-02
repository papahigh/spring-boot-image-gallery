package io.github.papahigh.types;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;


public record Blob(
        @NotNull String id,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt,
        @NotNull String contentType,
        @NotNull String fileName,
        @NotNull Long fileSize,
        @NotNull String blobPath,
        @NotNull String externalUrl) {
}
