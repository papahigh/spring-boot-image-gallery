package io.github.papahigh.types;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;


public record Location(
        @Nullable Double lat,
        @Nullable Double lon,
        @Nullable LocalDateTime timestamp) {
}
