package io.github.papahigh.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import static jakarta.persistence.FetchType.LAZY;


@Getter
@Entity
@Table(name = "image_class", indexes = @Index(name = "ix_image_class_image", columnList = "image_id"))
public class ImageClassModel extends IdentifiableModel {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "image_id", nullable = false, updatable = false)
    @Setter(onParam_ = @NotNull)
    private ImageModel image;

    @Column(name = "class_name", nullable = false, updatable = false)
    @Setter(onParam_ = @NotBlank)
    private String className;

    @Column(name = "probability", nullable = false, updatable = false)
    @Setter(onParam_ = @NotNull)
    private Double probability;

}
