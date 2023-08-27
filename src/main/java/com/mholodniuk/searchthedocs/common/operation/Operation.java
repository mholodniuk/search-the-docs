package com.mholodniuk.searchthedocs.common.operation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Operation {
    public static <T> void applyIfChanged(T oldValue, T newValue, FieldUpdater<T> updater) {
        if (newValue != null && !newValue.equals(oldValue)) {
            updater.updateIfChanged(newValue);
        }
    }
}
