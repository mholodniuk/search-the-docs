package com.mholodniuk.searchthedocs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
public class ElasticTest {

    @Container
    @ServiceConnection
    public static ElasticsearchContainer elasticsearch = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.3.1")
            .withEnv("discovery.type", "single-node")
            .withReuse(true);

    @Test
    void testDatabaseIsRunning() {
        assertThat(elasticsearch.isRunning()).isTrue();
    }
}
