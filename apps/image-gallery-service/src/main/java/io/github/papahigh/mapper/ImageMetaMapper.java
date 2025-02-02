package io.github.papahigh.mapper;

import io.github.papahigh.domain.ImageMetaModel;
import io.github.papahigh.domain.ImageModel;
import io.github.papahigh.types.MetaTag;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pipeline.image.ExtractMetadata;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;


@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface ImageMetaMapper {

    MetaTag toMetaTag(ImageMetaModel model);

    @Mapping(target = "image", expression = "java(imageModel)")
    ImageMetaModel fromMetadataTag(ExtractMetadata.MetadataTag metadataTag, @Context ImageModel imageModel);
}
