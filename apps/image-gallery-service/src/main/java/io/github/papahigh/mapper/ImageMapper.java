package io.github.papahigh.mapper;

import io.github.papahigh.domain.ImageModel;
import io.github.papahigh.types.ImageDetails;
import io.github.papahigh.types.ImageSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(
        componentModel = SPRING,
        uses = {BlobMapper.class, LocationMapper.class, ImageClassMapper.class, ImageMetaMapper.class}
)
public interface ImageMapper {

    @Mapping(target = "fileName", source = "originalFile.fileName")
    ImageSummary toImageSummary(ImageModel model);

    @Mapping(target = "fileName", source = "originalFile.fileName")
    ImageDetails toImageDetails(ImageModel model);

}
