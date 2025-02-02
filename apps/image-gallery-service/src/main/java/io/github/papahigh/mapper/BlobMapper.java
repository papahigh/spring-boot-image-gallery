package io.github.papahigh.mapper;

import io.github.papahigh.domain.BlobModel;
import io.github.papahigh.types.Blob;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface BlobMapper {

    Blob toBlob(BlobModel model);

}
