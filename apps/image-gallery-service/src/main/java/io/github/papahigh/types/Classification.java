package io.github.papahigh.types;

import org.jetbrains.annotations.NotNull;


public record Classification(@NotNull String className, double probability) {
}
