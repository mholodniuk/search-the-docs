package com.mholodniuk.searchthedocs.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public abstract class CommonUtils {
    public static String extractAfterLastDot(String input) {
        String[] parts = input.split("\\.");
        return (parts.length > 0) ? parts[parts.length - 1] : input;
    }

    public static UUID toUUID(String value) {
        return UUID.fromString(value);
    }

    @SafeVarargs
    public static <T> List<T> concatenate(List<T>... lists) {
        return Stream.of(lists)
                .flatMap(Collection::stream)
                .toList();
    }
}
