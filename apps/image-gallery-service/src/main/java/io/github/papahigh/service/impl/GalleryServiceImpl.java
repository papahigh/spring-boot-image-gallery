package io.github.papahigh.service.impl;

import io.github.papahigh.domain.ImageRepository;
import io.github.papahigh.mapper.ImageMapper;
import io.github.papahigh.service.GalleryService;
import io.github.papahigh.types.ImageDetails;
import io.github.papahigh.types.ImageSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ImageSummary> listImages(Pageable page) {
        var model = imageRepository.searchAllBy(page);
        return model.map(imageMapper::toImageSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ImageDetails> getImage(String itemId) {
        var model = imageRepository.findById(itemId);
        return model.map(imageMapper::toImageDetails);
    }
}
