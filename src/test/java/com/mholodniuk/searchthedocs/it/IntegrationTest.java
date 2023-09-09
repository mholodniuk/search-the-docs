package com.mholodniuk.searchthedocs.it;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
@ExtendWith(SpringExtension.class)
public abstract class IntegrationTest {
    @Container
    protected static ElasticsearchContainer elasticsearch = new ElasticTestContainer();

    @Container
    protected static PostgreSQLContainer<?> postgres
            = new PostgreSQLContainer<>("postgres:15.4-alpine").withCopyFileToContainer(
            MountableFile.forClasspathResource("/schema.sql"),
            "/docker-entrypoint-initdb.sql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void setUp() {
        postgres.start();
        elasticsearch.start();
    }

    @AfterAll
    static void destroy() {
        postgres.stop();
        elasticsearch.stop();
    }
}
