package pipeline.image;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pipeline.MediaType;
import pipeline.Pipeline.PipelineStep;
import pipeline.TempBlob;
import pipeline.UnitOfWork;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static pipeline.UnitOfWork.ArtifactNames.INPUT_ARTIFACT_NAME;


@Slf4j
@ToString
@RequiredArgsConstructor
public class ImageMagick implements PipelineStep {

    private final String inputName;
    private final String outputName;
    private final MediaType outputType;
    private final String resize;
    private final String quality;
    private final boolean strip;


    @Override
    public void process(UnitOfWork unitOfWork) throws IOException, InterruptedException {
        var input = unitOfWork.getArtifact(inputName, TempBlob.class);
        Objects.requireNonNull(input, "inputArtifact must not be null");

        var output = input.create(outputType);
        execute(command(input.path(), output.path()));
        unitOfWork.addArtifact(outputName, output);
    }

    private List<String> command(Path input, Path output) {
        return new ArrayList<>() {{
            add("magick");
            add(input.toString());

            if (strip) add("-strip");
            if (resize != null) addAll(List.of("-resize", resize));
            if (quality != null) addAll(List.of("-quality", quality));

            add(output.toString());
        }};
    }

    public static boolean isReady() {
        try {
            execute(List.of("magick", "-version"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void execute(List<String> command) throws IOException, InterruptedException {
        var exit = new ProcessBuilder().command(command).start().waitFor();
        if (exit != 0) {
            throw new RuntimeException("Error occurred while executing command: %s".formatted(command));
        }
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {

        private String inputName = INPUT_ARTIFACT_NAME;
        private String outputName;
        private MediaType outputType;
        private String resize;
        private String quality;
        private boolean strip = true;

        Builder inputName(@NotNull String artifactName) {
            this.inputName = artifactName;
            return this;
        }

        Builder outputName(@NotNull String artifactName) {
            this.outputName = artifactName;
            return this;
        }

        Builder outputType(@NotNull MediaType mediaType) {
            this.outputType = mediaType;
            return this;
        }

        Builder resize(@Nullable String resize) {
            this.resize = resize;
            return this;
        }

        Builder quality(@Nullable String quality) {
            this.quality = quality;
            return this;
        }

        Builder strip(boolean strip) {
            this.strip = strip;
            return this;
        }

        ImageMagick build() {
            Objects.requireNonNull(inputName, "inputName must not be null");
            Objects.requireNonNull(outputName, "outputName must not be null");
            Objects.requireNonNull(outputType, "outputType must not be null");
            return new ImageMagick(inputName, outputName, outputType, resize, quality, strip);
        }
    }
}
