package pipeline.image;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.translate.TranslateException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pipeline.Pipeline.PipelineStep;
import pipeline.TempBlob;
import pipeline.UnitOfWork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;


@Slf4j
@ToString(exclude = {"predictors"})
@RequiredArgsConstructor
public class ClassifyImage implements PipelineStep {

    private final int topK;
    private final String inputName;
    private final String outputName;
    private final ThreadLocal<Predictor<Image, Classifications>> predictors;


    @Override
    public void process(UnitOfWork unitOfWork) throws IOException {

        var input = unitOfWork.getArtifact(inputName, TempBlob.class);
        Objects.requireNonNull(input, "inputArtifact must not be null");

        try {

            var image = ImageFactory.getInstance().fromFile(input.path());
            var result = predictors.get().predict(image);

            var output = new ArrayList<ImageClass>(topK);
            for (var it : result.topK(topK)) {
                output.add(new ImageClass(it.getClassName(), it.getProbability()));
            }

            unitOfWork.addArtifact(outputName, new ImageClasses(output));

        } catch (TranslateException e) {
            log.error("Error occurred while classifying image", e);
        }
    }

    public record ImageClasses(List<ImageClass> items) {
        public ImageClasses {
            items = List.copyOf(items);
        }
    }

    public record ImageClass(String className, double probability) {
    }

    static final int DEFAULT_TOP_K_VALUE = 5;

    static Builder builder() {
        return new Builder();
    }

    @NoArgsConstructor(access = PRIVATE)
    static class Builder {

        private int topK = DEFAULT_TOP_K_VALUE;
        private String inputName;
        private String outputName;
        private ThreadLocal<Predictor<Image, Classifications>> predictors;

        Builder topK(int topK) {
            this.topK = topK;
            return this;
        }

        Builder inputName(@NotNull String artifactName) {
            this.inputName = artifactName;
            return this;
        }

        Builder outputName(@NotNull String artifactName) {
            this.outputName = artifactName;
            return this;
        }

        Builder predictorFactory(@NotNull Supplier<Predictor<Image, Classifications>> supplier) {
            this.predictors = ThreadLocal.withInitial(supplier);
            return this;
        }

        ClassifyImage build() {
            Objects.requireNonNull(inputName, "inputName must not be null");
            Objects.requireNonNull(outputName, "outputName name must not be null");
            Objects.requireNonNull(predictors, "predictorFactory must not be null");
            return new ClassifyImage(topK, inputName, outputName, predictors);
        }
    }
}
