package io.github.papahigh.domain;

import io.github.papahigh.types.ImageBundle;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.LAZY;


@Getter
@Entity
@Table(name = "image")
public class ImageModel extends IdentifiableModel {

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(onParam_ = @NotNull)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Setter(onParam_ = @NotNull)
    private LocalDateTime updatedAt;

    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "geo_lat")),
            @AttributeOverride(name = "lon", column = @Column(name = "geo_lon")),
            @AttributeOverride(name = "timestamp", column = @Column(name = "geo_date"))
    })
    @Setter(onParam_ = @Nullable)
    private LocationModel location;

    @Type(JsonBinaryType.class)
    @Column(name = "thumbnail", nullable = false, columnDefinition = "JSONB")
    @Setter(onParam_ = @NotNull)
    private ImageBundle thumbnail;

    @Type(JsonBinaryType.class)
    @Column(name = "full_size", nullable = false, columnDefinition = "JSONB")
    @Setter(onParam_ = @NotNull)
    private ImageBundle fullSize;

    @OneToMany(mappedBy = "image", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @OrderBy("probability DESC")
    @Setter(onParam_ = @NotNull)
    private List<ImageClassModel> classes = new ArrayList<>();

    @OneToMany(mappedBy = "image", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @OrderBy("directory,tagName")
    @Setter(onParam_ = @NotNull)
    private List<ImageMetaModel> metadata = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = PERSIST)
    @JoinColumn(name = "original_file_id")
    @Setter(onParam_ = @NotNull)
    private BlobModel originalFile;


    @PrePersist
    void onCreated() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdated() {
        updatedAt = LocalDateTime.now();
    }
}
