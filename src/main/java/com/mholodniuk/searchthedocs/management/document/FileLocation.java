package com.mholodniuk.searchthedocs.management.document;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class FileLocation {
    private String storageProvider;
    private String path;
}
