package io.github.papahigh.types;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;


public record ImageSummary(
        @Nonnull String id,
        @Nonnull String fileName,
        @Nullable Location location,
        @Nonnull LocalDateTime createdAt,
        @Nonnull LocalDateTime updatedAt,
        @Nonnull ImageBundle thumbnail) {
}
