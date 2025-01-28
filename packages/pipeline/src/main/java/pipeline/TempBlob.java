package pipeline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;


public record TempBlob(
        @NotNull Path path,
        @NotNull String name,
        @Nullable MediaType mediaType
) {

    public Path directory() {
        return path.getParent();
    }

    public InputStream inputStream() throws IOException {
        return Files.newInputStream(path);
    }

    public OutputStream outputStream() throws IOException {
        return Files.newOutputStream(path);
    }

    public long size() throws IOException {
        return Files.size(path);
    }

    public TempBlob alt(Path path) {
        return new Builder(path).contentType(mediaType).build();
    }

    @Override
    public String toString() {
        return "TempBlob[%s]".formatted(name);
    }

    public static TempBlob of(Path path, MediaType mediaType) {
        return new Builder(path).contentType(mediaType).build();
    }

    public static Builder builder(String name) throws IOException {
        return new Builder(name);
    }

    public static class Builder {

        private final Path path;
        private final String name;
        private MediaType mediaType;

        private Builder(Path path) {
            this.path = path;
            this.name = path.getFileName().toString();
        }

        private Builder(@NotNull String name) throws IOException {
            var dir = Files.createTempDirectory("unitOfWork");
            this.path = dir.resolve(name);
            this.name = name;
        }

        public Builder content(@NotNull InputStream inputStream) throws IOException {
            try (inputStream) {
                Files.copy(inputStream, path);
                return this;
            }
        }

        public Builder contentType(@Nullable MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public TempBlob build() {
            return new TempBlob(path, name, mediaType);
        }
    }
}
