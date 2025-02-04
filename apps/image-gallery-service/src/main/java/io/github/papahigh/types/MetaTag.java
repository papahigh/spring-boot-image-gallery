package io.github.papahigh.types;

import jakarta.annotation.Nonnull;


public record MetaTag(
        @Nonnull String directory,
        @Nonnull String tagName,
        @Nonnull String tagValue) {
}
