package io.github.papahigh.config;

import ai.djl.MalformedModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.CenterCrop;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;


@Configuration
public class ClassificationConfig {

    @Value("classpath:pytorch_models/resnet18")
    private Resource resource;

    @Bean(destroyMethod = "close")
    public ZooModel<Image, Classifications> classificationModel() throws IOException, ModelNotFoundException, MalformedModelException {
        return Criteria.builder()
                .setTypes(Image.class, Classifications.class)
                .optModelPath(resource.getFile().toPath())
                .optOption("mapLocation", "true")
                .optTranslator(translator())
                .build()
                .loadModel();
    }

    @Bean
    public ImageClassificationTranslator translator() {
        return ImageClassificationTranslator.builder()
                .addTransform(new CenterCrop(224, 224))
                .addTransform(new ToTensor())
                .addTransform(new Normalize(
                        new float[]{0.485f, 0.456f, 0.406f},
                        new float[]{0.229f, 0.224f, 0.225f}))
                .optApplySoftmax(true)
                .build();
    }
}
