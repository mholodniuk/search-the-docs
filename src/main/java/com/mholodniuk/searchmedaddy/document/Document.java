package com.mholodniuk.searchmedaddy.document;

public record Document(
        String name,
        String text,
        Integer page) {
}
