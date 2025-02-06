package io.github.papahigh.types;

import jakarta.annotation.Nonnull;


public record Classification(@Nonnull String id, @Nonnull String className, double probability) {
}
