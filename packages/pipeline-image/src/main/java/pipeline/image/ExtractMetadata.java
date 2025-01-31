package pipeline.image;

import com.drew.imaging.FileType;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pipeline.MediaType;
import pipeline.Pipeline.PipelineStep;
import pipeline.TempBlob;
import pipeline.UnitOfWork;

import java.io.IOException;
import java.util.*;

import static com.drew.imaging.FileType.*;
import static lombok.AccessLevel.PRIVATE;
import static pipeline.MediaType.*;
import static pipeline.UnitOfWork.ArtifactNames.INPUT_ARTIFACT_NAME;
import static pipeline.UnitOfWork.ArtifactNames.METADATA_ARTIFACT_NAME;


@Slf4j
@ToString
@RequiredArgsConstructor
public class ExtractMetadata implements PipelineStep {

    private final String inputName;
    private final String outputName;


    @Override
    public void process(UnitOfWork unitOfWork) throws IOException {
        var input = unitOfWork.getArtifact(inputName, TempBlob.class);
        Objects.requireNonNull(input, "inputArtifact must not be null");

        try {

            var fileType = MAPPINGS.getOrDefault(input.mediaType(), Unknown);
            var metadata = ImageMetadataReader.readMetadata(input.inputStream(), input.size(), fileType);

            var output = Metadata.builder();
            for (var it : metadata.getDirectories()) {
                if (it instanceof GpsDirectory gps) {
                    output.gps(GpsMetadata.of(gps));
                } else {
                    for (var tag : it.getTags()) {
                        output.tag(MetadataTag.of(tag));
                    }
                }
            }

            unitOfWork.addArtifact(outputName, output.build());
        } catch (ImageProcessingException e) {
            log.error("Error occurred while extracting image metadata", e);
        }
    }

    private static final Map<MediaType, FileType> MAPPINGS = new EnumMap<>(MediaType.class) {{
        put(IMAGE_AVIF, QuickTime);
        put(IMAGE_JPEG, Jpeg);
        put(IMAGE_PNG, Png);
        put(IMAGE_WEBP, WebP);
        put(IMAGE_HEIC, Heif);
        put(IMAGE_HEIF, Heif);
        put(IMAGE_TIFF, Tiff);
    }};

    @lombok.Builder
    public record Metadata(GpsMetadata gps, @Singular List<MetadataTag> tags) {
        public List<MetadataTag> tags() {
            return List.copyOf(tags);
        }
    }

    public record GpsMetadata(double latitude, double longitude, Date date) {
        static GpsMetadata of(GpsDirectory gps) {
            var loc = gps.getGeoLocation();
            return new GpsMetadata(loc.getLatitude(), loc.getLongitude(), gps.getGpsDate());
        }
    }

    public record MetadataTag(String directory, String tag, String value) {
        static MetadataTag of(Tag tag) {
            return new MetadataTag(tag.getDirectoryName(), tag.getTagName(), tag.getDescription());
        }
    }


    static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = PRIVATE)
    static class Builder {

        private String inputName = INPUT_ARTIFACT_NAME;
        private String outputName = METADATA_ARTIFACT_NAME;

        Builder inputName(@NotNull String artifactName) {
            this.inputName = artifactName;
            return this;
        }

        Builder outputName(@NotNull String artifactName) {
            this.outputName = artifactName;
            return this;
        }

        PipelineStep build() {
            Objects.requireNonNull(inputName, "inputName must not be null");
            Objects.requireNonNull(outputName, "outputName must not be null");
            return new ExtractMetadata(inputName, outputName);
        }
    }
}
