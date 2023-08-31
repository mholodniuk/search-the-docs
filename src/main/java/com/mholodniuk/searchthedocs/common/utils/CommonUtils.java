package com.mholodniuk.searchthedocs.common.utils;

import java.util.UUID;

public abstract class CommonUtils {
    public static String extractAfterLastDot(String input) {
        String[] parts = input.split("\\.");
        return (parts.length > 0) ? parts[parts.length - 1] : input;
    }
    public static UUID toUUID(String value) {
        return UUID.fromString(value);
    }
}
