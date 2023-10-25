package com.mholodniuk.searchthedocs.document.model;

public record SearchableUser(Long id, String username, String displayName, String email) {
}