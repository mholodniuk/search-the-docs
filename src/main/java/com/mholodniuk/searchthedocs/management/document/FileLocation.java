package com.mholodniuk.searchthedocs.management.document;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Table(name = "file_locations")
@NoArgsConstructor @Getter @Setter
public class FileLocation {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "storage_type", nullable = false)
    private String storageType;

    @Column(name = "file_path", nullable = false)
    private String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FileLocation that = (FileLocation) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
