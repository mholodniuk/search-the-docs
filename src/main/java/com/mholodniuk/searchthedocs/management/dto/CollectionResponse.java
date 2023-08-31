package com.mholodniuk.searchthedocs.management.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.Map;

public record CollectionResponse<T>(
        @JsonIgnore
        String name,
        @JsonIgnore
        Collection<T> collection
) {
    @JsonAnyGetter
    public Map<String, Object> jsonify() {
        return Map.of(name, collection, "count", collection.size());
    }
}
