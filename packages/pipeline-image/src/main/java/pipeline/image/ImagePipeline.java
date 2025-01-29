package pipeline.image;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import pipeline.MediaType;
import pipeline.Pipeline;
import pipeline.Pipeline.PipelineStep;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;


@NoArgsConstructor(access = PRIVATE)
public final class ImagePipeline {

    public static PipelineBuilder builder() {
        return new PipelineBuilder();
    }

    @NoArgsConstructor(access = PRIVATE)
    public static final class PipelineBuilder {

        private final List<PipelineStep> steps = new ArrayList<>();

        /**
         * Creates and returns a builder for configuring and adding a {@link ClassifyImage} step
         * to the pipeline. The ClassifyImage step performs image classification using a specified
         * predictor and outputs the classification results.
         *
         * @return an instance of {@link ClassifyImageBuilder} to configure and add the image classification step
         * to the pipeline.
         */
        public ClassifyImageBuilder classifyImage() {
            return new ClassifyImageBuilder();
        }

        /**
         * Creates and returns a builder for configuring and adding an {@link ExtractMetadata} step
         * to the pipeline. The ExtractMetadata step allows for metadata extraction from an input
         * artifact and produces metadata as an output artifact.
         *
         * @return an instance of {@link ExtractMetadataBuilder} to configure and add the metadata extraction step
         * to the pipeline.
         */
        public ExtractMetadataBuilder extractMetadata() {
            return new ExtractMetadataBuilder();
        }

        /**
         * Creates and returns a builder for configuring and adding an {@link ImageMagick} step
         * to the pipeline. The ImageMagick step allows for versatile image processing capabilities,
         * such as resizing, quality adjustment, and artifact manipulation.
         *
         * @return an instance of {@link ImageMagickBuilder} to configure and add the ImageMagick
         * processing step to the pipeline.
         */
        public ImageMagickBuilder imageMagick() {
            return new ImageMagickBuilder();
        }

        public Pipeline build() {
            return new Pipeline(steps);
        }

        class PipelineStepBuilder {
            protected void addStep(PipelineStep step) {
                PipelineBuilder.this.steps.add(step);
            }

            public Pipeline build() {
                return PipelineBuilder.this.build();
            }
        }

        public class ClassifyImageBuilder extends PipelineStepBuilder {
            private final ClassifyImage.Builder builder = ClassifyImage.builder();

            public ClassifyImageBuilder topK(int topK) {
                builder.topK(topK);
                return this;
            }

            public ClassifyImageBuilder inputName(@NonNull String artifactName) {
                builder.inputName(artifactName);
                return this;
            }

            public ClassifyImageBuilder outputName(@NonNull String artifactName) {
                builder.outputName(artifactName);
                return this;
            }

            public ClassifyImageBuilder predictorFactory(@NonNull Supplier<Predictor<Image, Classifications>> factory) {
                builder.predictorFactory(factory);
                return this;
            }

            public PipelineBuilder and() {
                addStep(builder.build());
                return PipelineBuilder.this;
            }
        }

        public class ExtractMetadataBuilder extends PipelineStepBuilder {
            private final ExtractMetadata.Builder builder = ExtractMetadata.builder();

            public ExtractMetadataBuilder inputName(@NonNull String artifactName) {
                builder.inputName(artifactName);
                return this;
            }

            public ExtractMetadataBuilder outputName(@NonNull String artifactName) {
                builder.outputName(artifactName);
                return this;
            }

            public PipelineBuilder and() {
                addStep(builder.build());
                return PipelineBuilder.this;
            }
        }

        public class ImageMagickBuilder extends PipelineStepBuilder {
            private final ImageMagick.Builder builder = ImageMagick.builder();

            public ImageMagickBuilder inputName(@NonNull String artifactName) {
                builder.inputName(artifactName);
                return this;
            }

            public ImageMagickBuilder outputName(@NonNull String artifactName) {
                builder.outputName(artifactName);
                return this;
            }

            public ImageMagickBuilder outputType(@NonNull MediaType mediaType) {
                builder.outputType(mediaType);
                return this;
            }

            public ImageMagickBuilder resize(@Nullable String resize) {
                builder.resize(resize);
                return this;
            }

            public ImageMagickBuilder quality(@Nullable String quality) {
                builder.quality(quality);
                return this;
            }

            public ImageMagickBuilder strip(boolean strip) {
                builder.strip(strip);
                return this;
            }

            public ImageMagickBuilder strip() {
                builder.strip(true);
                return this;
            }

            public PipelineBuilder and() {
                addStep(builder.build());
                return PipelineBuilder.this;
            }
        }
    }
}
