package io.github.papahigh.types;

import org.jetbrains.annotations.NotNull;


public record MetaTag(
        @NotNull String directory,
        @NotNull String tagName,
        @NotNull String tagValue) {
}
