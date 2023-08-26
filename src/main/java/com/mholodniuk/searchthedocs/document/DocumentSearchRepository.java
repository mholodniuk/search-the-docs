package com.mholodniuk.searchthedocs.document;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
interface DocumentSearchRepository extends ElasticsearchRepository<SearchableDocument, String> {
}
