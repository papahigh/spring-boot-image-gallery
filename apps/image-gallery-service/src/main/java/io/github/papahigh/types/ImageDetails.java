package io.github.papahigh.types;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;


public record ImageDetails(
        @Nonnull String id,
        @Nonnull String fileName,
        @Nullable Location location,
        @Nonnull LocalDateTime createdAt,
        @Nonnull LocalDateTime updatedAt,
        @Nonnull ImageBundle thumbnail,
        @Nonnull ImageBundle fullSize,
        @Nonnull Blob originalFile,
        @Nonnull List<MetaTag> metadata,
        @Nonnull List<Classification> classes) {
}
