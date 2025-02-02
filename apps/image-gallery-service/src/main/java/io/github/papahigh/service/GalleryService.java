package io.github.papahigh.service;

import io.github.papahigh.types.ImageDetails;
import io.github.papahigh.types.ImageSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface GalleryService {

    Page<ImageSummary> listImages(Pageable page);

    Optional<ImageDetails> getImage(String itemId);

}
