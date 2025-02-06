package io.github.papahigh.service.impl;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.ZooModel;
import blobstore.BlobStore;
import blobstore.BlobStore.BlobStoreException;
import io.github.papahigh.domain.BlobModel;
import io.github.papahigh.domain.ImageModel;
import io.github.papahigh.domain.ImageRepository;
import io.github.papahigh.mapper.ImageClassMapper;
import io.github.papahigh.mapper.ImageMapper;
import io.github.papahigh.mapper.ImageMetaMapper;
import io.github.papahigh.mapper.LocationMapper;
import io.github.papahigh.service.UploadService;
import io.github.papahigh.types.ImageBundle;
import io.github.papahigh.types.ImageDetails;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pipeline.MediaType;
import pipeline.Pipeline;
import pipeline.TempBlob;
import pipeline.UnitOfWork;
import pipeline.image.ClassifyImage.ImageClasses;
import pipeline.image.ExtractMetadata.Metadata;
import pipeline.image.ImagePipeline;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public abstract class UploadServiceImpl implements UploadService {

    private final Pipeline pipeline;
    private final BlobStore blobStore;
    private final ImageRepository imageRepository;

    private final ImageMapper imageMapper;
    private final LocationMapper locationMapper;
    private final ImageMetaMapper imageMetaMapper;
    private final ImageClassMapper imageClassMapper;

    @Lookup
    abstract UploadServiceImpl getSelf();

    @Transactional
    ImageDetails saveImage(@NotNull ImageModel image) {
        log.debug("Saving image model {}", image);
        imageRepository.save(image);
        log.info("Saved image model {}", image);
        return imageMapper.toImageDetails(image);
    }

    @Async("io")
    @Override
    public CompletableFuture<ImageDetails> upload(@NotNull TempBlob tempBlob) throws IOException, BlobStoreException {
        log.info("Uploading file '{}'", tempBlob.name());

        try (var unitOfWork = pipeline.process(tempBlob)) {

            var image = new ImageModel();

            copyUnitOfWork(unitOfWork, image);
            copyOriginalFile(tempBlob, image);

            var details = getSelf().saveImage(image);
            return completedFuture(details);
        }
    }


    private void copyUnitOfWork(UnitOfWork unit, ImageModel image) throws BlobStoreException {

        var metadata = unit.getArtifact(Util.METADATA_OUTPUT_NAME, Metadata.class);
        if (metadata != null) {
            image.setLocation(locationMapper.fromGpsMetadata(metadata.gps()));
            image.setMetadata(metadata.tags().stream().map(it -> imageMetaMapper.fromMetadataTag(it, image)).toList());
        }

        var classes = unit.getArtifact(Util.CLASSIFY_OUTPUT_NAME, ImageClasses.class);
        image.setClasses(classes.items().stream().map(it -> imageClassMapper.fromImageClass(it, image)).toList());

        image.setThumbnail(
                ImageBundle.builder()
                        .avif(copyBlob(unit, Util.AVIF_THUMBNAIL))
                        .jpeg(copyBlob(unit, Util.JPEG_THUMBNAIL))
                        .webp(copyBlob(unit, Util.WEBP_THUMBNAIL))
                        .build()
        );

        image.setFullSize(
                ImageBundle.builder()
                        .avif(copyBlob(unit, Util.AVIF_FULL_SIZE))
                        .jpeg(copyBlob(unit, Util.JPEG_FULL_SIZE))
                        .webp(copyBlob(unit, Util.WEBP_FULL_SIZE))
                        .build()
        );
    }

    private void copyOriginalFile(TempBlob tempBlob, ImageModel image) throws BlobStoreException, IOException {

        log.debug("Copying '{}' to blobstore", tempBlob.name());
        var response = blobStore.putBlob(Util.getBlobKey(tempBlob), tempBlob.path());

        var model = new BlobModel();
        model.setBlobPath(response.blobPath().toString());
        model.setExternalUrl(response.externalUrl());
        model.setContentType(tempBlob.mediaType().typeName);
        model.setFileSize(tempBlob.size());
        model.setFileName(tempBlob.name());

        image.setOriginalFile(model);
    }

    private String copyBlob(UnitOfWork unitOfWork, String artifactName) throws BlobStoreException {

        var blob = unitOfWork.getArtifact(artifactName, TempBlob.class);
        if (blob == null) {
            log.error("Artifact '{}' not found", artifactName);
            throw new RuntimeException("Artifact '%s' not found".formatted(artifactName));
        }

        log.trace("Copying '{}' to blobstore", artifactName);
        var response = blobStore.putBlob(Util.getBlobKey(blob), blob.path());
        return response.externalUrl();
    }

    private interface Util {

        String THUMBNAIL_WIDTH = "600";
        String FULL_SIZE_WIDTH = "1200";
        String THUMBNAIL_QUALITY = "80%";
        String FULL_SIZE_QUALITY = "80%";

        String AVIF_THUMBNAIL = "AVIF_THUMBNAIL";
        String JPEG_THUMBNAIL = "JPEG_THUMBNAIL";
        String WEBP_THUMBNAIL = "WEBP_THUMBNAIL";
        String AVIF_FULL_SIZE = "AVIF_FULL_SIZE";
        String JPEG_FULL_SIZE = "JPEG_FULL_SIZE";
        String WEBP_FULL_SIZE = "WEBP_FULL_SIZE";

        String CLASSIFY_INPUT_RESIZE = "256x256";
        String CLASSIFY_INPUT_QUALITY = "80%";

        String CLASSIFY_INPUT_NAME = "CLASSIFY_INPUT_NAME";
        String CLASSIFY_OUTPUT_NAME = "CLASSIFY_OUTPUT_NAME";
        String METADATA_OUTPUT_NAME = "METADATA_OUTPUT_NAME";

        static String getBlobKey(String blobName) {
            return "images/" + blobName;
        }

        static String getBlobKey(TempBlob blob) {
            return getBlobKey(blob.path().getFileName().toString());
        }
    }

    @Component
    static class PipelineFactory implements FactoryBean<Pipeline> {

        @Setter(onMethod_ = @Autowired)
        private ZooModel<Image, Classifications> classificationModel;

        @Override
        public Pipeline getObject() {
            // @formatter:off
            return ImagePipeline.builder()

                    .imageMagick()
                        .outputName(Util.AVIF_THUMBNAIL)
                        .outputType(MediaType.IMAGE_AVIF)
                        .quality(Util.THUMBNAIL_QUALITY)
                        .resize(Util.THUMBNAIL_WIDTH)
                        .strip()
                    .and()

                    .imageMagick()
                        .outputName(Util.AVIF_FULL_SIZE)
                        .outputType(MediaType.IMAGE_AVIF)
                        .quality(Util.FULL_SIZE_QUALITY)
                        .resize(Util.FULL_SIZE_WIDTH)
                        .strip()
                    .and()

                    .imageMagick()
                        .outputName(Util.JPEG_THUMBNAIL)
                        .outputType(MediaType.IMAGE_JPEG)
                        .quality(Util.THUMBNAIL_QUALITY)
                        .resize(Util.THUMBNAIL_WIDTH)
                        .strip()
                    .and()

                    .imageMagick()
                        .outputName(Util.JPEG_FULL_SIZE)
                        .outputType(MediaType.IMAGE_JPEG)
                        .quality(Util.FULL_SIZE_QUALITY)
                        .resize(Util.FULL_SIZE_WIDTH)
                        .strip()
                    .and()

                    .imageMagick()
                        .outputName(Util.WEBP_THUMBNAIL)
                        .outputType(MediaType.IMAGE_WEBP)
                        .quality(Util.THUMBNAIL_QUALITY)
                        .resize(Util.THUMBNAIL_WIDTH)
                        .strip()
                    .and()

                    .imageMagick()
                        .outputName(Util.WEBP_FULL_SIZE)
                        .outputType(MediaType.IMAGE_WEBP)
                        .quality(Util.FULL_SIZE_QUALITY)
                        .resize(Util.FULL_SIZE_WIDTH)
                        .strip()
                    .and()

                    .imageMagick()
                        .outputName(Util.CLASSIFY_INPUT_NAME)
                        .outputType(MediaType.IMAGE_JPEG)
                        .resize(Util.CLASSIFY_INPUT_RESIZE)
                        .quality(Util.CLASSIFY_INPUT_QUALITY)
                    .and()

                .classifyImage()
                    .inputName(Util.CLASSIFY_INPUT_NAME)
                    .outputName(Util.CLASSIFY_OUTPUT_NAME)
                    .predictorFactory(classificationModel::newPredictor)
                .and()

                .extractMetadata()
                    .outputName(Util.METADATA_OUTPUT_NAME)

                .build();
            // @formatter:on;
        }

        @Override
        public Class<?> getObjectType() {
            return Pipeline.class;
        }
    }
}
