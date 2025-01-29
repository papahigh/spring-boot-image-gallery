package pipeline.image;

import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import pipeline.TempBlob;
import pipeline.UnitOfWork;
import pipeline.image.ClassifyImage.ImageClasses;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pipeline.MediaType.IMAGE_JPEG;
import static pipeline.UnitOfWork.ArtifactNames.INPUT_ARTIFACT_NAME;
import static pipeline.image.ClassifyImage.DEFAULT_TOP_K_VALUE;
import static rocks.cleancode.hamcrest.record.HasFieldMatcher.field;


public class ClassifyImageTest {

    private static final String INPUT_IMAGE_NAME = "images/jpg/dog.jpg";
    private static final String OUTPUT_ARTIFACT_NAME = "output";

    private static final TestPredictorFactory factory = new TestPredictorFactory();

    @AfterAll
    static void after() {
        factory.close();
    }

    @Test
    void testClassification() throws IOException {

        var unit = UnitOfWork.of(getInput());
        var step = ClassifyImage.builder()
                .inputName(INPUT_ARTIFACT_NAME)
                .outputName(OUTPUT_ARTIFACT_NAME)
                .predictorFactory(factory::create)
                .build();

        step.process(unit);

        var output = unit.getArtifact(OUTPUT_ARTIFACT_NAME, ImageClasses.class);

        assertEquals(DEFAULT_TOP_K_VALUE, output.items().size());
        assertThat(output.items(), everyItem(
                allOf(
                        field("className", notNullValue()),
                        field("probability", greaterThan(0.0))
                )
        ));
    }

    @Test
    void testTopKParamValue() throws IOException {

        var topK = new Random().nextInt(25) + 1;
        var unit = UnitOfWork.of(getInput());
        var step = ClassifyImage.builder()
                .inputName(INPUT_ARTIFACT_NAME)
                .outputName(OUTPUT_ARTIFACT_NAME)
                .predictorFactory(factory::create)
                .topK(topK)
                .build();

        step.process(unit);

        var output = unit.getArtifact(OUTPUT_ARTIFACT_NAME, ImageClasses.class);

        assertEquals(topK, output.items().size());
        assertThat(output.items(), everyItem(
                allOf(
                        field("className", notNullValue()),
                        field("probability", greaterThan(0.0))
                )
        ));
    }

    private static TempBlob getInput() throws IOException {
        return TempBlob.builder(INPUT_IMAGE_NAME)
                .mediaType(IMAGE_JPEG)
                .content(Files.newInputStream(getResource(INPUT_IMAGE_NAME)))
                .build();
    }

    @SneakyThrows
    private static Path getResource(String name) {
        var uri = ClassifyImageTest.class.getClassLoader().getResource(name).toURI();
        return Path.of(uri);
    }

    static class TestPredictorFactory implements AutoCloseable {

        private final ZooModel<Image, Classifications> model = createZooModel();

        public Predictor<Image, Classifications> create() {
            return model.newPredictor();
        }

        @Override
        public void close() {
            model.close();
        }

        @SneakyThrows
        private ZooModel<Image, Classifications> createZooModel() {
            return Criteria.builder()
                    .setTypes(Image.class, Classifications.class)
                    .optModelPath(getResource("pytorch_models/resnet18"))
                    .optOption("mapLocation", "true")
                    .optTranslator(translator())
                    .build()
                    .loadModel();
        }

        private ImageClassificationTranslator translator() {
            return ImageClassificationTranslator.builder()
                    .addTransform(new Resize(256))
                    .addTransform(new CenterCrop(224, 224))
                    .addTransform(new ToTensor())
                    .addTransform(new Normalize(
                            new float[]{0.485f, 0.456f, 0.406f},
                            new float[]{0.229f, 0.224f, 0.225f}))
                    .optApplySoftmax(true)
                    .build();
        }
    }
}
