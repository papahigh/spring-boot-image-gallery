package pipeline.image;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import pipeline.MediaType;
import pipeline.TempBlob;
import pipeline.UnitOfWork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pipeline.MediaType.IMAGE_JPEG;
import static pipeline.MediaType.IMAGE_WEBP;


public class ImageMagicTest {

    @Test
    @DisabledIf(
            value = "pipeline.image.ImageMagicTest#isDisabled",
            disabledReason = "ImageMagic is not available!"
    )
    void testConvertJpeg() throws IOException, InterruptedException {

        var blob = getBlob("images/jpg/gps/DSCN0010.jpg", IMAGE_JPEG);
        var step = ImageMagick.builder()
                .outputName("THUMBNAIL")
                .outputType(IMAGE_JPEG)
                .strip(true)
                .quality("80%")
                .resize("500x500")
                .build();

        try (var unit = UnitOfWork.of(blob)) {
            step.process(unit);

            assertTrue(unit.hasArtifact("THUMBNAIL"));
            assertTrue(Files.exists(unit.getArtifact("THUMBNAIL", TempBlob.class).path()));
        }
    }

    @Test
    @DisabledIf(
            value = "pipeline.image.ImageMagicTest#isDisabled",
            disabledReason = "ImageMagic is not available!"
    )
    void testConvertWebp() throws IOException, InterruptedException {

        var blob = getBlob("images/webp/DSCN0010.webp", IMAGE_WEBP);
        var step = ImageMagick.builder()
                .outputName("THUMBNAIL")
                .outputType(IMAGE_WEBP)
                .strip(true)
                .quality("80%")
                .resize("500x500")
                .build();


        try (var unit = UnitOfWork.of(blob)) {

            step.process(unit);

            assertTrue(unit.hasArtifact("THUMBNAIL"));
            assertTrue(Files.exists(unit.getArtifact("THUMBNAIL", TempBlob.class).path()));
        }
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
        var uri = ImageMagicTest.class.getClassLoader().getResource(resourceName).toURI();
        return Path.of(uri);
    }

    static boolean isDisabled() {
        return !ImageMagick.isReady();
    }
}
