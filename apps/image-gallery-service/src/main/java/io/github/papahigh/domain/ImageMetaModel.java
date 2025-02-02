package io.github.papahigh.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import static jakarta.persistence.FetchType.LAZY;


@Entity
@Getter
@Table(name = "image_meta", indexes = @Index(name = "ix_image_meta_image", columnList = "image_id"))
public class ImageMetaModel extends IdentifiableModel {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "image_id", nullable = false, updatable = false)
    @Setter(onParam_ = @NotNull)
    private ImageModel image;

    @Column(name = "directory", nullable = false, updatable = false)
    @Setter(onParam_ = @NotBlank)
    private String directory;

    @Column(name = "tag_name", nullable = false, updatable = false)
    @Setter(onParam_ = @NotBlank)
    private String tagName;

    @Column(name = "tag_value", nullable = false, updatable = false)
    @Setter(onParam_ = @NotNull)
    private String tagValue;

}
