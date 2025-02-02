package io.github.papahigh.mapper;

import io.github.papahigh.domain.LocationModel;
import io.github.papahigh.types.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pipeline.image.ExtractMetadata;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;


@Mapper(componentModel = SPRING)
public interface LocationMapper {

    Location toLocation(LocationModel model);

    @Mappings({
            @Mapping(source = "latitude", target = "lat"),
            @Mapping(source = "longitude", target = "lon"),
            @Mapping(source = "date", target = "timestamp"),
    })
    LocationModel fromGpsMetadata(ExtractMetadata.GpsMetadata metadata);

}
