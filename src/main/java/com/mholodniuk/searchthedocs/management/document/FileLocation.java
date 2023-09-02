package com.mholodniuk.searchthedocs.management.document;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FileLocation {
    @Column(nullable = false)
    private String storageProvider;
    @Column(nullable = false)
    private String path;
}
