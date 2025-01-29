package pipeline;

import io.hypersistence.tsid.TSID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.apache.commons.io.FilenameUtils.getExtension;


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

    public TempBlob create(MediaType mediaType) {
        var path = directory().resolve(mediaType.randomFilename());
        return new Builder(path).mediaType(mediaType).build();
    }

    public long size() throws IOException {
        return Files.size(path);
    }

    @Override
    public String toString() {
        return "TempBlob[%s]".formatted(path);
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
            var dir = Files.createTempDirectory("tempBlob");
            this.path = dir.resolve(randomRename(name));
            this.name = name;
        }

        public Builder content(@NotNull InputStream inputStream) throws IOException {
            try (inputStream) {
                Files.copy(inputStream, path);
                return this;
            }
        }

        public Builder mediaType(@Nullable MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public TempBlob build() {
            return new TempBlob(path, name, mediaType);
        }
    }

    public static String randomRename(@Nullable String fileName) {
        return randomFilename(getExtension(fileName));
    }

    public static String randomFilename(@Nullable String extension) {
        var randomName = TSID.Factory.getTsid().toString();
        if (extension == null || extension.isEmpty())
            return randomName;
        return "%s.%s".formatted(randomName, extension);
    }
}
