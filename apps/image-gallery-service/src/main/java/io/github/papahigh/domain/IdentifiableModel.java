package io.github.papahigh.domain;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


@Getter
@ToString(of = "id")
@MappedSuperclass
public class IdentifiableModel {

    @Id
    @Tsid
    @Column(name = "id", nullable = false, columnDefinition = "CHAR(13)")
    @Setter(onParam_ = @NotNull)
    private String id;


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        return getId() != null && Objects.equals(getId(), ((IdentifiableModel) o).getId());
    }

    @Override
    public final int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
