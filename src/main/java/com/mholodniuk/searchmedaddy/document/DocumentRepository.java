package com.mholodniuk.searchmedaddy.document;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

interface DocumentRepository extends ElasticsearchRepository<SearchableDocument, String> {
}
