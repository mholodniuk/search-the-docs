package com.mholodniuk.searchthedocs.management.folder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mholodniuk.searchthedocs.management.document.Document;
import com.mholodniuk.searchthedocs.management.customer.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "owner_id"})
)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Room {
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "private")
    private Boolean isPrivate;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime modifiedAt;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private Customer owner;

    @OneToMany(
            mappedBy = "room",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    @ToString.Exclude
    private Set<Document> documents;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Room room = (Room) o;
        return id != null && Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
