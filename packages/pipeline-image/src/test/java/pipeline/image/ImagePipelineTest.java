package pipeline.image;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import pipeline.TempBlob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static pipeline.MediaType.IMAGE_JPEG;
import static pipeline.MediaType.IMAGE_WEBP;
import static pipeline.UnitOfWork.ArtifactNames.INPUT_ARTIFACT_NAME;
import static pipeline.UnitOfWork.ArtifactNames.METADATA_ARTIFACT_NAME;


public class ImagePipelineTest {

    private static final ClassifyImageTest.TestPredictorFactory factory = new ClassifyImageTest.TestPredictorFactory();

    @AfterAll
    static void after() {
        factory.close();
    }

    @Test
    @DisabledIf(
            value = "pipeline.image.ImagePipelineTest#isDisabled",
            disabledReason = "ImageMagic is not available!"
    )
    void testPipeline() throws IOException {

        // @formatter:off
        var pipeline = ImagePipeline.builder()
                .extractMetadata()
                .and()
                .imageMagick()
                    .outputName("THUMBNAIL_JPG")
                    .outputType(IMAGE_JPEG)
                    .resize("300")
                    .quality("80%")
                    .strip()
                .and()
                .imageMagick()
                    .outputName("THUMBNAIL_WEBP")
                    .outputType(IMAGE_WEBP)
                    .resize("300")
                    .quality("80%")
                    .strip()
                .and()
                .imageMagick()
                    .outputName("CLASSIFY_INPUT")
                    .outputType(IMAGE_JPEG)
                    .resize("256x256")
                    .quality("80%")
                    .strip()
                .and()
                .classifyImage()
                    .inputName("CLASSIFY_INPUT")
                    .outputName("CLASSIFICATION")
                    .predictorFactory(factory::create)
                    .topK(10)
                .and()
                .build();
        // @formatter:on

        var blob = TempBlob.builder("DSCN0021.jpg")
                .mediaType(IMAGE_JPEG)
                .content(Files.newInputStream(getResource("images/jpg/gps/DSCN0021.jpg")))
                .build();

        try (var output = pipeline.process(blob)) {
            assertAll(
                    () -> assertTrue(output.hasArtifact(INPUT_ARTIFACT_NAME)),
                    () -> assertTrue(output.hasArtifact(METADATA_ARTIFACT_NAME)),
                    () -> assertTrue(output.hasArtifact("THUMBNAIL_JPG")),
                    () -> assertTrue(output.hasArtifact("THUMBNAIL_WEBP")),
                    () -> assertTrue(output.hasArtifact("CLASSIFY_INPUT")),
                    () -> assertTrue(output.hasArtifact("CLASSIFICATION"))
            );
        }
    }


    @SneakyThrows
    private static Path getResource(String name) {
        var uri = ImagePipelineTest.class.getClassLoader().getResource(name).toURI();
        return Path.of(uri);
    }

    static boolean isDisabled() {
        return !ImageMagick.isReady();
    }
}
