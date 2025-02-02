package io.github.papahigh.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;


public record ImageDetails(
        @NotNull String id,
        @NotNull String fileName,
        @Nullable Location location,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt,
        @NotNull ImageBundle thumbnail,
        @NotNull ImageBundle fullSize,
        @NotNull Blob originalFile,
        @NotNull List<MetaTag> metadata,
        @NotNull List<Classification> classes) {
}
