package io.github.papahigh.domain;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;


@Embeddable
public record LocationModel(Double lat, Double lon, LocalDateTime timestamp) {
}
