package com.mholodniuk.searchthedocs.management.document;

import com.mholodniuk.searchthedocs.management.user.User;
import com.mholodniuk.searchthedocs.management.room.Room;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "documents")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Document {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Type(ListArrayType.class)
    @Column(name = "tags", columnDefinition = "text[]")
    private List<String> tags;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "path", column = @Column(name = "file_path")),
            @AttributeOverride(name = "storageProvider", column = @Column(name = "storage_destination")),
    })
    private FileLocation fileLocation;
}
