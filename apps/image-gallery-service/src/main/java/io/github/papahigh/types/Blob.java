package io.github.papahigh.types;

import jakarta.annotation.Nonnull;

import java.time.LocalDateTime;


public record Blob(
        @Nonnull String id,
        @Nonnull LocalDateTime createdAt,
        @Nonnull LocalDateTime updatedAt,
        @Nonnull String contentType,
        @Nonnull String fileName,
        @Nonnull Long fileSize,
        @Nonnull String blobPath,
        @Nonnull String externalUrl) {
}
