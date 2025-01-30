package blobstore;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BlobPathTest {

    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(
            value = {
                    "BLOB_PATH          | PARENT_PATH   | IS_EMPTY  ",
                    "xyz/abc/123.mp3    | xyz/abc       | false     ",
                    "123.mp3            | ''            | true      ",
                    "''                 | ''            | true      ",
                    "//                 | ''            | true      ",
                    "/                  | ''            | true      ",
            },
            delimiter = '|',
            useHeadersInDisplayName = true
    )
    void testParent(String blobPath, String parentPath, boolean isEmpty) {
        var parent = BlobPath.of(blobPath).parent();

        assertEquals(BlobPath.of(parentPath), parent);
        assertEquals(isEmpty, parent.isEmpty());
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(
            value = {
                    "BLOB_PATH          | LAST_PART     ",
                    "xyz/abc/123.mp3    | 123.mp3       ",
                    "xyz/abc/123        | 123           ",
                    "''                 | ''            ",
                    "//                 | ''            ",
                    "/                  | ''            ",
            },
            delimiter = '|',
            useHeadersInDisplayName = true
    )
    void testLastPart(String blobPath, String lastPart) {
        assertEquals(lastPart, BlobPath.of(blobPath).lastPart());
    }

    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(
            value = {
                    "ORIGINAL_PATH      | ARGUMENT      | FINAL_PATH      ",
                    "xyz/               | abc/123.mp3   | xyz/abc/123.mp3 ",
                    "xyz/abc/           | 123.mp3       | xyz/abc/123.mp3 ",
                    "xyz/abc/           | ''            | xyz/abc         ",
                    "''                 | 123.mp3       | 123.mp3         ",
                    "/                  | 123.mp3       | 123.mp3         ",
                    "//                 | ''            | ''              ",
            },
            delimiter = '|',
            useHeadersInDisplayName = true
    )
    void testResolve(String originalPath, String argument, String expectedPath) {
        assertEquals(BlobPath.of(expectedPath), BlobPath.of(originalPath).resolve(argument));
    }
}
