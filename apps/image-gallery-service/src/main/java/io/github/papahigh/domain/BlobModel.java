package io.github.papahigh.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "blob")
public class BlobModel extends IdentifiableModel {

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(onParam_ = @NotNull)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Setter(onParam_ = @NotNull)
    private LocalDateTime updatedAt;

    @Column(name = "content_type", nullable = false, updatable = false)
    @Setter(onParam_ = @NotBlank)
    private String contentType;

    @Column(name = "file_name", nullable = false, updatable = false)
    @Setter(onParam_ = @NotBlank)
    private String fileName;

    @Column(name = "file_size", nullable = false, updatable = false)
    @Setter(onParam_ = {@NotNull, @Positive})
    private Long fileSize;

    @Column(name = "blob_path", nullable = false, updatable = false)
    @Setter(onParam_ = @NotBlank)
    private String blobPath;

    @Column(name = "external_url", nullable = false)
    @Setter(onParam_ = @NotBlank)
    private String externalUrl;


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
