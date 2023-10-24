package com.mholodniuk.searchthedocs.document.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchableRoom {
    private Long id;
    private String name;
}