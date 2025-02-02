package io.github.papahigh.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;


public record ImageSummary(
        @NotNull String id,
        @NotNull String fileName,
        @Nullable Location location,
        @NotNull LocalDateTime createdAt,
        @NotNull LocalDateTime updatedAt,
        @NotNull ImageBundle thumbnail) {
}
