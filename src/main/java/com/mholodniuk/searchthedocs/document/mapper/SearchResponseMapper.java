package com.mholodniuk.searchthedocs.document.mapper;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mholodniuk.searchthedocs.document.model.SearchableDocument;
import com.mholodniuk.searchthedocs.document.utils.FieldAttr;
import com.mholodniuk.searchthedocs.document.dto.PhraseSearchResponse;
import com.mholodniuk.searchthedocs.document.dto.SingleSearchResponse;

public class SearchResponseMapper {
    public static PhraseSearchResponse mapToDto(SearchResponse<SearchableDocument> response) {
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

                            return new SingleSearchResponse(
                                    document.getName(),
                                    document.getDocumentId(),
                                    document.getRoom(),
                                    document.getPage(),
                                    phraseHits,
                                    phraseHits.size());
                        })
                        .toList());
    }
}
