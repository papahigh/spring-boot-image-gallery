package pipeline;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pipeline.MediaType.IMAGE_WEBP;


public class UnitOfWorkTest {

    @Test
    void testUnitOfWorkClose() throws IOException {
        var blob = getBlob("blobs/3840x2160.jpg", IMAGE_WEBP);
        var unitOfWork = UnitOfWork.of(blob);

        var input = unitOfWork.input();

        assertTrue(Files.exists(input.path()));
        assertTrue(Files.exists(input.directory()));

        unitOfWork.close();

        assertFalse(Files.exists(input.path()));
        assertFalse(Files.exists(input.directory()));
    }


    @SneakyThrows
    private static TempBlob getBlob(String fileName, MediaType mediaType) {
        return TempBlob.builder(fileName)
                .mediaType(mediaType)
                .content(Files.newInputStream(getResource(fileName)))
                .build();
    }

    @SneakyThrows
    private static Path getResource(String resourceName) {
        var uri = UnitOfWorkTest.class.getClassLoader().getResource(resourceName).toURI();
        return Path.of(uri);
    }
}
