package com.mholodniuk.searchthedocs.management.access;

import com.mholodniuk.searchthedocs.management.user.User;
import com.mholodniuk.searchthedocs.management.room.Room;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "access_keys")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AccessKey {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "rights", nullable = false)
    private AccessRight rights;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    @ToString.Exclude
    private User participant;
}