package io.github.papahigh.api;


import io.github.papahigh.service.GalleryService;
import io.github.papahigh.types.ImageDetails;
import io.github.papahigh.types.ImageSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping
    public Page<ImageSummary> listImages(Pageable page) {
        return galleryService.listImages(page);
    }

    @GetMapping("/{itemId}")
    public ImageDetails viewImage(@PathVariable String itemId) {
        return galleryService.getImage(itemId).orElseThrow(HttpNotFoundException::new);
    }

    static class HttpNotFoundException extends ResponseStatusException {
        HttpNotFoundException() {
            super(HttpStatusCode.valueOf(404));
        }
    }
}
