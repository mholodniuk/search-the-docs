package com.mholodniuk.searchthedocs.document;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSearchRepository extends ElasticsearchRepository<SearchableDocument, String> {
    void deleteByDocumentId(String documentId);
}
