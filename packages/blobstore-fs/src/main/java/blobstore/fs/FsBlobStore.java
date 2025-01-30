package blobstore.fs;

import blobstore.BlobPath;
import blobstore.BlobStore;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.file.Counters.PathCounters;
import org.apache.commons.io.file.PathUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static lombok.AccessLevel.PRIVATE;


@Slf4j
@RequiredArgsConstructor(access = PRIVATE)
public class FsBlobStore implements BlobStore {

    private final Path rootPath;
    private final PathResolver pathResolver;
    private final ExternalUrlProvider urlProvider;

    @Override
    public GetBlobResponse getBlob(@NotNull BlobPath blobPath) throws BlobStoreException {
        return new GetBlobResponse() {
            @Override
            public @NotNull InputStream inputStream() throws BlobStoreException {
                try {
                    return Files.newInputStream(resolvePath(blobPath));
                } catch (IOException e) {
                    log.error("Failed to open input stream '{}'", blobPath, e);
                    throw new BlobStoreException("Failed to open input stream '%s'".formatted(blobPath), e);
                }
            }
        };
    }

    @Override
    public PutBlobResponse putBlob(@NotNull String key, @NotNull Path inputPath) throws BlobStoreException {
        try (var inputStream = Files.newInputStream(inputPath)) {
            var blobPath = BlobPath.of(key);
            var resolved = resolvePath(blobPath);
            Files.createDirectories(resolved.getParent());
            Files.copy(inputStream, resolved, REPLACE_EXISTING);
            return new FsPutBlobResponse(blobPath);
        } catch (IOException e) {
            log.error("Failed to put blob '{}' into {}", key, inputPath, e);
            throw new BlobStoreException("Failed to put blob '%s' into %s".formatted(key, inputPath), e);
        }
    }

    @Override
    public DeleteBlobResponse deleteBlob(@NotNull BlobPath blobPath) {
        try {
            var counters = PathUtils.delete(resolvePath(blobPath));
            return FsDeleteBlobResponse.of(blobPath, counters);
        } catch (IOException e) {
            log.error("Failed to delete blob '{}'", blobPath, e);
            return FsDeleteBlobResponse.failed(blobPath);
        }
    }

    @ToString
    @RequiredArgsConstructor
    class FsPutBlobResponse implements PutBlobResponse {
        @NotNull
        private final BlobPath blobPath;

        @Override
        public @NotNull BlobPath blobPath() {
            return blobPath;
        }

        @Override
        public @NotNull String externalUrl() {
            return urlProvider.getURL(blobPath);
        }
    }

    record FsDeleteBlobResponse(@NotNull BlobPath blobPath, boolean deleted) implements DeleteBlobResponse {
        static FsDeleteBlobResponse of(@NotNull BlobPath blobPath, PathCounters counters) {
            var deleted = !(counters.getFileCounter().get() == 0 && counters.getDirectoryCounter().get() == 0);
            return new FsDeleteBlobResponse(blobPath, deleted);
        }

        static FsDeleteBlobResponse ok(@NotNull BlobPath blobPath) {
            return new FsDeleteBlobResponse(blobPath, true);
        }

        static FsDeleteBlobResponse failed(@NotNull BlobPath blobPath) {
            return new FsDeleteBlobResponse(blobPath, false);
        }
    }

    @RequiredArgsConstructor
    static class PathResolver {
        private final int trieLength;

        public Path resolve(@NotNull BlobPath blobPath) {
            var pathTrie = makePathTrie(blobPath.lastPart());
            return Path.of(blobPath.parent().resolve(pathTrie).toString());
        }

        private BlobPath makePathTrie(String fileName) {
            var parts = new ArrayList<String>();
            var data = fileName.toCharArray();
            for (int i = 0; i < Math.min(data.length, trieLength); i++) {
                if (Character.isLetterOrDigit(data[i])) {
                    parts.add(Character.toString(data[i]));
                } else {
                    parts.add("next");
                }
            }
            parts.add(fileName);
            return new BlobPath(parts);
        }
    }

    static class ExternalUrlProvider {
        private final String baseUrl;

        ExternalUrlProvider(@NotNull String baseUrl) {
            if (baseUrl.isEmpty())
                throw new IllegalArgumentException("baseUrl must not be blank");
            if (!baseUrl.endsWith("/"))
                this.baseUrl = baseUrl + "/";
            else
                this.baseUrl = baseUrl;
        }

        public String getURL(BlobPath blobPath) {
            return baseUrl + blobPath.toString();
        }
    }

    Path resolvePath(BlobPath blobPath) {
        return rootPath.resolve(pathResolver.resolve(blobPath));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Path rootPath;
        private String baseUrl;
        private int trieLength = 4;

        public Builder rootPath(@NotNull Path rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public Builder baseUrl(@NotNull String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder trieLength(int trieLength) {
            this.trieLength = trieLength;
            return this;
        }

        public FsBlobStore build() {
            Objects.requireNonNull(rootPath, "rootPath must not be null");
            Objects.requireNonNull(baseUrl, "baseUrl must not be null");
            return new FsBlobStore(rootPath, new PathResolver(trieLength), new ExternalUrlProvider(baseUrl));
        }
    }
}

