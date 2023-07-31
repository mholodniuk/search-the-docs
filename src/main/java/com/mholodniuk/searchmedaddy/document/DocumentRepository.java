package com.mholodniuk.searchmedaddy.document;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DocumentRepository extends ElasticsearchRepository<Document, String> {
}
