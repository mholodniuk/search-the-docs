package com.mholodniuk.searchthedocs.document;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.HighlighterType;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import com.mholodniuk.searchthedocs.document.model.SearchableDocument;
import com.mholodniuk.searchthedocs.document.utils.FieldAttr;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
class SearchService {
    private final ElasticsearchClient elasticsearchClient;

    //    todo: look at this
//    https://stackoverflow.com/questions/61581529/spring-data-elastic-search-query-highlight
    SearchResponse<SearchableDocument> searchDocumentsByPhrase(String phrase, List<Long> roomIds) throws IOException {
        TermsQueryField roomTerms = new TermsQueryField.Builder()
                .value(roomIds.stream().map(FieldValue::of).toList())
                .build();

        return elasticsearchClient.search(search -> search
                        .index(SearchableDocument.getIndexName())
                        .source(SourceConfig.of(sc -> sc.filter(f -> f.excludes(List.of(FieldAttr.Document.TEXT_FIELD, "_class")))))
                        .highlight(Highlight.of(h -> h
                                .fields(FieldAttr.Document.TEXT_FIELD, HighlightField.of(hf -> hf
                                        .fragmentSize(69)
                                        .preTags("<b>")
                                        .postTags("</b>"))
                                )
                                .type(HighlighterType.Unified)))
                        .query(q -> q
                                .bool(b -> b
                                        .must(m -> m.match(t -> t.field(FieldAttr.Document.TEXT_FIELD).query(phrase)))
                                        .filter(f -> f
                                                .terms(t -> t
                                                        .field(FieldAttr.Document.ROOM_ID_FIELD)
                                                        .terms(roomTerms)
                                                )
                                        )
                                )
                        ),
                SearchableDocument.class);
    }
}
