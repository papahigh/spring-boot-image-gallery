package io.github.papahigh.types;

import jakarta.annotation.Nonnull;


public record Classification(@Nonnull String className, double probability) {
}
