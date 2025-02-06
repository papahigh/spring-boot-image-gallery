package io.github.papahigh.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ImageRepository extends JpaRepository<ImageModel, String> {

    @EntityGraph(attributePaths = { "originalFile" })
    Page<ImageModel> searchAllByOrderByCreatedAtDesc(Pageable page);

}
