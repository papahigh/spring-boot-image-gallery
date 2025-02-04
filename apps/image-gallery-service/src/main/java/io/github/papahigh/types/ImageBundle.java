package io.github.papahigh.types;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;


public record ImageBundle(
        @Nullable String avif,
        @Nullable String jpeg,
        @Nullable String webp
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String avif;
        private String jpeg;
        private String webp;

        public Builder avif(@Nonnull String avif) {
            this.avif = avif;
            return this;
        }

        public Builder jpeg(@Nonnull String jpeg) {
            this.jpeg = jpeg;
            return this;
        }

        public Builder webp(@Nonnull String webp) {
            this.webp = webp;
            return this;
        }

        public ImageBundle build() {
            return new ImageBundle(avif, jpeg, webp);
        }
    }
}
