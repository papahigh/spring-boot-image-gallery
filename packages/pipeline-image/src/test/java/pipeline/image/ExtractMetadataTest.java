package pipeline.image;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import pipeline.MediaType;
import pipeline.TempBlob;
import pipeline.UnitOfWork;
import pipeline.image.ExtractMetadata.Metadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.*;
import static pipeline.MediaType.*;
import static pipeline.UnitOfWork.ArtifactNames.METADATA_ARTIFACT_NAME;


public class ExtractMetadataTest {


    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(
            value = {
                    "IMAGE                          | LATITUDE  | LONGITUDE ",
                    "images/jpg/gps/DSCN0010.jpg    | 43.467448 | 11.885127 ",
                    "images/jpg/gps/DSCN0012.jpg    | 43.467157 | 11.885395 ",
                    "images/jpg/gps/DSCN0021.jpg    | 43.467082 | 11.884538 ",
                    "images/jpg/gps/DSCN0025.jpg    | 43.468365 | 11.881635 ",
                    "images/jpg/gps/DSCN0027.jpg    | 43.468442 | 11.881515 ",
                    "images/jpg/gps/DSCN0029.jpg    | 43.468243 | 11.880172 ",
                    "images/jpg/gps/DSCN0038.jpg    | 43.467255 | 11.879213 ",
                    "images/jpg/gps/DSCN0040.jpg    | 43.466012 | 11.879112 ",
                    "images/jpg/gps/DSCN0042.jpg    | 43.464455 | 11.881478 "
            },
            delimiter = '|',
            useHeadersInDisplayName = true
    )
    void testExtractGPS(String fileName, double latitude, double longitude) throws Exception {

        var step = ExtractMetadata.builder().build();
        var blob = getBlob(fileName, IMAGE_JPEG);

        try (var unit = UnitOfWork.of(blob)) {

            step.process(unit);

            var output = unit.getArtifact(METADATA_ARTIFACT_NAME, Metadata.class);

            assertEquals(latitude, output.gps().latitude(), 1e-6);
            assertEquals(longitude, output.gps().longitude(), 1e-6);
        }
    }

    @Test
    void testExtractAvif() throws Exception {

        var step = ExtractMetadata.builder().build();
        var blob = getBlob("images/avif/3840x2160.avif", IMAGE_AVIF);

        try (var unit = UnitOfWork.of(blob)) {

            step.process(unit);

            var output = unit.getArtifact(METADATA_ARTIFACT_NAME, Metadata.class);
            assertFalse(output.tags().isEmpty());
        }
    }

    @Test
    void testExtractPng() throws Exception {

        var step = ExtractMetadata.builder().build();
        var blob = getBlob("images/png/DSCN0010.png", IMAGE_PNG);

        try (var unit = UnitOfWork.of(blob)) {

            step.process(unit);

            var output = unit.getArtifact(METADATA_ARTIFACT_NAME, Metadata.class);

            assertEquals(43.467448, output.gps().latitude(), 1e-6);
            assertEquals(11.885126, output.gps().longitude(), 1e-6);
        }
    }

    @Test
    void testExtractWebp() throws Exception {

        var step = ExtractMetadata.builder().build();
        var blob = getBlob("images/webp/DSCN0010.webp", IMAGE_WEBP);

        try (var unit = UnitOfWork.of(blob)) {

            step.process(unit);

            var output = unit.getArtifact(METADATA_ARTIFACT_NAME, Metadata.class);

            assertEquals(43.467448, output.gps().latitude(), 1e-6);
            assertEquals(11.885126, output.gps().longitude(), 1e-6);
        }
    }

    @Test
    void testExtractHeic() throws Exception {

        var step = ExtractMetadata.builder().build();
        var blob = getBlob("images/heic/mobile/HMD_Nokia_8.3_5G_hdr.heif", IMAGE_HEIC);

        try (var unit = UnitOfWork.of(blob)) {

            step.process(unit);

            var output = unit.getArtifact(METADATA_ARTIFACT_NAME, Metadata.class);

            assertEquals(40.662993, output.gps().latitude(), 1e-6);
            assertEquals(-3.763740, output.gps().longitude(), 1e-6);
        }
    }

    @Test
    void testExtractCustomized() throws Exception {

        var CUSTOM_INPUT = "custom_input";
        var CUSTOM_OUTPUT = "custom_output";

        var step = ExtractMetadata.builder().inputName(CUSTOM_INPUT).outputName(CUSTOM_OUTPUT).build();
        var blob = getBlob("images/jpg/dog.jpg", IMAGE_JPEG);

        try (var unit = UnitOfWork.of(blob)) {

            unit.addArtifact(CUSTOM_INPUT, blob);
            step.process(unit);

            assertTrue(unit.hasArtifact(CUSTOM_INPUT));
            assertTrue(unit.hasArtifact(CUSTOM_OUTPUT));
            assertFalse(unit.hasArtifact(METADATA_ARTIFACT_NAME));
        }
    }


    @ParameterizedTest(name = "[{index}] {arguments}")
    @CsvSource(
            value = {
                    "IMAGE                                      | MEDIA_TYPE ",
                    "images/heic/mobile/HMD_Nokia_8.3_5G.heif   | image/jpeg ",
                    "images/tiff/DudleyLeavittUtah.tiff         | image/webp ",
            },
            delimiter = '|',
            useHeadersInDisplayName = true
    )
    void testInvalidFileFormat(String fileName, String mediaType) throws IOException {

        var blob = getBlob(fileName, mediaType);
        var step = ExtractMetadata.builder().build();

        try (var unit = UnitOfWork.of(blob)) {

            assertDoesNotThrow(() -> step.process(unit));

            var output = unit.hasArtifact(METADATA_ARTIFACT_NAME);
            assertFalse(output);
        }
    }


    @ParameterizedTest(name = "[{index}] {arguments}")
    @ValueSource(
            strings = {
                    "images/jpg/invalid/image00971.jpg",
                    "images/jpg/invalid/image01088.jpg",
                    "images/jpg/invalid/image01137.jpg",
                    "images/jpg/invalid/image01551.jpg",
                    "images/jpg/invalid/image01713.jpg",
                    "images/jpg/invalid/image01980.jpg",
                    "images/jpg/invalid/image02206.jpg"
            }
    )
    void testInvalidMetadata(String fileName) throws IOException {

        var blob = getBlob(fileName, IMAGE_JPEG);
        var step = ExtractMetadata.builder().build();

        try (var unit = UnitOfWork.of(blob)) {

            assertDoesNotThrow(() -> step.process(unit));

            var output = unit.getArtifact(METADATA_ARTIFACT_NAME, Metadata.class);
            assertThat(output.tags(), not(empty()));
        }
    }

    private static TempBlob getBlob(String fileName, String mediaType) {
        return getBlob(fileName, MediaType.ofTypeName(mediaType));
    }

    @SneakyThrows
    private static TempBlob getBlob(String fileName, MediaType mediaType) {
        return TempBlob.builder(fileName)
                .mediaType(mediaType)
                .content(Files.newInputStream(getResource(fileName)))
                .build();
    }

    @SneakyThrows
    private static Path getResource(String name) {
        var uri = ExtractMetadataTest.class.getClassLoader().getResource(name).toURI();
        return Path.of(uri);
    }
}
