package com.mholodniuk.searchthedocs.common.operation;

public interface FieldUpdater<T> {
    void updateIfChanged(T newValue);
}
