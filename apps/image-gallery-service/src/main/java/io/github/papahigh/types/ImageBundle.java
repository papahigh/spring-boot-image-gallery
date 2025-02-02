package io.github.papahigh.types;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.EnumMap;

import static io.github.papahigh.types.ImageBundle.ImageFormat.*;


public record ImageBundle(
        @Nullable String avif,
        @Nullable String jpeg,
        @Nullable String webp
) {

    public enum ImageFormat {
        AVIF,
        JPEG,
        WEBP,
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final Constructor<?> ctor = ImageBundle.class.getConstructors()[0];

        private final EnumMap<ImageFormat, String> bundle = new EnumMap<>(ImageFormat.class) {{
            for (var format : ImageFormat.values())
                put(format, null);
        }};

        public Builder avif(@NotNull String assets) {
            return add(AVIF, assets);
        }

        public Builder jpeg(@NotNull String assets) {
            return add(JPEG, assets);
        }

        public Builder webp(@NotNull String assets) {
            return add(WEBP, assets);
        }

        public Builder add(@NotNull ImageFormat format, @NotNull String assets) {
            bundle.put(format, assets);
            return this;
        }

        @SneakyThrows
        public ImageBundle build() {
            return (ImageBundle) ctor.newInstance(bundle.values().toArray());
        }
    }
}
