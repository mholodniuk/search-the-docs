package com.mholodniuk.searchmedaddy.document.mapper;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mholodniuk.searchmedaddy.document.Document;
import com.mholodniuk.searchmedaddy.document.utils.FieldAttr;
import com.mholodniuk.searchmedaddy.document.dto.PhraseSearchResponse;
import com.mholodniuk.searchmedaddy.document.dto.SingleSearchResponse;

public class SearchResponseMapper {
    public static PhraseSearchResponse mapToDto(SearchResponse<Document> response) {
        return new PhraseSearchResponse(
                response.hits().hits()
                        .stream()
                        .filter(hit -> hit.source() != null)
                        .map(hit -> {
                            var document = hit.source();
                            var phraseHits = hit.highlight().get(FieldAttr.Document.TEXT_FIELD)
                                    .stream()
                                    .map(result -> result.replace("\n", " "))
                                    .toList();

                            return new SingleSearchResponse(document.getName(), document.getPage(), phraseHits, phraseHits.size());
                        })
                        .toList());
    }
}
