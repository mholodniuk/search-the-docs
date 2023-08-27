package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.management.folder.Room;
import com.mholodniuk.searchthedocs.management.customer.Customer;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documents")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Type(ListArrayType.class)
    @Column(name = "tags", columnDefinition = "text[]")
    private List<String> tags;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @ToString.Exclude
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private Customer owner;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "path", column = @Column(name = "file_path")),
            @AttributeOverride(name = "storageProvider", column = @Column(name = "storage_destination")),
    })
    private FileLocation fileLocation;
}
