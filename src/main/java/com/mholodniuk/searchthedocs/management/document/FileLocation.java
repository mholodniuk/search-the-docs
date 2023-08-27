package com.mholodniuk.searchthedocs.management.document;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FileLocation {
    @Column(nullable = false)
    private String storageProvider;
    @Column(nullable = false)
    private String path;
}
