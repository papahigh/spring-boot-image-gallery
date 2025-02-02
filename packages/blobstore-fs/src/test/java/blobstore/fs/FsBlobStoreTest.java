package blobstore.fs;

import blobstore.BlobPath;
import blobstore.BlobStore.BlobStoreException;
import blobstore.fs.FsBlobStore.ExternalUrlProvider;
import blobstore.fs.FsBlobStore.PathResolver;
import lombok.SneakyThrows;
import org.apache.commons.io.file.PathUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;


public class FsBlobStoreTest {

    private static final String BASE_URL = "http://localhost:8080/uploads/";
    private Path rootPath;

    @BeforeEach
    void setUp() throws IOException {
        rootPath = Files.createTempDirectory("fsBlobStoreTest");
    }

    @AfterEach
    void tearDown() throws IOException {
        PathUtils.deleteDirectory(rootPath);
    }

    @Test
    void testPutBlobSuccess() throws BlobStoreException, IOException {

        var blobStore = getBlobStore();
        var putResponse = blobStore.putBlob("gallery/nested/level/blob.txt", getPath("blob.txt"));

        assertEquals(BlobPath.of("gallery/nested/level/blob.txt"), putResponse.blobPath());
        assertEquals(BASE_URL + "gallery/nested/level/blob.txt", putResponse.externalUrl());

        var expectedContent = Files.readString(getPath("blob.txt"));
        var actualContent = Files.readString(blobStore.resolvePath(putResponse.blobPath()));

        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testPutBlobFailure() {
        assertThrows(BlobStoreException.class, () -> getBlobStore().putBlob("gallery/blob.txt", Path.of("unknown/path")));
    }

    @Test
    void testGetBlobSuccess() throws BlobStoreException, IOException {

        var blobStore = getBlobStore();
        var putResponse = blobStore.putBlob("gallery/blob.txt", getPath("blob.txt"));
        var getResponse = blobStore.getBlob(putResponse.blobPath());

        try (var inputStream = getResponse.inputStream()) {
            var expectedContent = Files.readString(getPath("blob.txt"));
            var actualContent = new String(inputStream.readAllBytes(), UTF_8);

            assertEquals(expectedContent, actualContent);
        }
    }

    @Test
    void testGetBlobFailure() throws BlobStoreException {

        var getResponse = getBlobStore().getBlob(BlobPath.of("unknown/path"));

        assertThrows(IOException.class, getResponse::inputStream);
    }

    @Test
    void testDeleteBlobSuccess() throws BlobStoreException {

        var blobStore = getBlobStore();
        var putResponse = blobStore.putBlob("gallery/blob.txt", getPath("blob.txt"));
        var deleteResponse = blobStore.deleteBlob(putResponse.blobPath());

        assertTrue(deleteResponse.deleted());
        assertFalse(Files.exists(blobStore.resolvePath(putResponse.blobPath())));
    }

    @Test
    void testDeleteBlobFailure() {

        var blobStore = getBlobStore();
        var deleteResponse = blobStore.deleteBlob(BlobPath.of("unknown/path"));

        assertFalse(deleteResponse.deleted());
    }


    private FsBlobStore getBlobStore() {
        return FsBlobStore.builder().baseUrl(BASE_URL).rootPath(rootPath).build();
    }

    @Nested
    class PathResolverTest {

        @ParameterizedTest(name = "[{index}] {arguments}")
        @CsvSource(
                value = {
                        "TRIE_LENGTH    | ORIGINAL_PATH             | RESOLVED_PATH",
                        "5              | gallery/aa/bb/image.jpg   | gallery/aa/bb/i/m/a/g/e/image.jpg",
                        "4              | gallery/aa/bb/image.jpg   | gallery/aa/bb/i/m/a/g/image.jpg",
                        "3              | gallery/aa/bb/image.jpg   | gallery/aa/bb/i/m/a/image.jpg",
                        "2              | gallery/aa/bb/image.jpg   | gallery/aa/bb/i/m/image.jpg",
                        "1              | gallery/aa/bb/image.jpg   | gallery/aa/bb/i/image.jpg",
                        "4              | gallery/aa/bb/i.jpg       | gallery/aa/bb/i/next/j/p/i.jpg",
                        "4              | gallery/aa/bb/!@#$.jpg    | gallery/aa/bb/next/next/next/next/!@#$.jpg",
                        "0              | gallery/aa/bb/image.jpg   | gallery/aa/bb/image.jpg",
                        "-1             | gallery/aa/bb/image.jpg   | gallery/aa/bb/image.jpg",
                },
                delimiter = '|',
                useHeadersInDisplayName = true
        )
        void testPathResolver(int trieLength, String originalPath, String expectedPath) {
            var resolver = new PathResolver(trieLength);
            var actualPath = resolver.resolve(BlobPath.of(originalPath));

            assertEquals(Path.of(expectedPath), actualPath);
        }
    }


    @Nested
    class ExternalUrlProviderTest {

        @ParameterizedTest(name = "[{index}] {arguments}")
        @CsvSource(
                value = {
                        "BASE_URL                       | BLOB_PATH                 | EXTERNAL_URL                                  ",
                        "http://localhost:8080/uploads  | abc/xyz/img.jpg           | http://localhost:8080/uploads/abc/xyz/img.jpg ",
                        "https://img.com/               | avatar/12345.jpg          | https://img.com/avatar/12345.jpg              "
                },
                delimiter = '|',
                useHeadersInDisplayName = true
        )
        void testExternalUrlProvider(String baseUrl, String blobPath, String expectedUrl) {
            var provider = new ExternalUrlProvider(baseUrl);
            var actualUrl = provider.getURL(BlobPath.of(blobPath));

            assertEquals(expectedUrl, actualUrl);
        }

        @Test
        void testBlankBaseUrl() {
            assertThrows(IllegalArgumentException.class, () -> new ExternalUrlProvider(""));
        }
    }


    @SneakyThrows
    private static Path getPath(String name) {
        var uri = FsBlobStoreTest.class.getClassLoader().getResource(name).toURI();
        return Path.of(uri);
    }
}
