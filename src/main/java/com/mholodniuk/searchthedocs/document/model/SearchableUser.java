package com.mholodniuk.searchthedocs.document.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchableUser {
    private Long id;
    private String username;
    private String displayName;
    private String email;
}