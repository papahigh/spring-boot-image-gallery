package io.github.papahigh.mapper;

import io.github.papahigh.domain.ImageClassModel;
import io.github.papahigh.domain.ImageModel;
import io.github.papahigh.types.Classification;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pipeline.image.ClassifyImage;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;


@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public interface ImageClassMapper {

    Classification toClassification(ImageClassModel model);

    @Mapping(target = "image", expression = "java(imageModel)")
    ImageClassModel fromImageClass(ClassifyImage.ImageClass imageClass, @Context ImageModel imageModel);

}
