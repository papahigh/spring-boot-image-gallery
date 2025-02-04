package io.github.papahigh.types;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;


public record Location(
        @Nullable Double lat,
        @Nullable Double lon,
        @Nullable LocalDateTime timestamp) {
}
