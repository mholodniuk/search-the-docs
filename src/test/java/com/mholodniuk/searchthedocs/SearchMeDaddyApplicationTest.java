//package com.mholodniuk.searchthedocs;
//
//import com.mholodniuk.searchthedocs.document.DocumentSearchRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
//import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
//import org.testcontainers.elasticsearch.ElasticsearchContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.lifecycle.Startables;
//import org.testcontainers.utility.MountableFile;
//
//import java.time.Duration;
//
//@DataElasticsearchTest
//@Testcontainers(disabledWithoutDocker = true)
//// todo: why cant elasticsearchrepository load up ??
////@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class SearchMeDaddyApplicationTest {
//
//    @Autowired
//    private DocumentSearchRepository documentSearchRepository;
//
//    @Container
//    static PostgreSQLContainer<?> postgres
//            = new PostgreSQLContainer<>("postgres:15-alpine").withCopyFileToContainer(
//            MountableFile.forClasspathResource("/schema.sql"),
//            "/docker-entrypoint-initdb.sql");
//
//    @Container
//    @ServiceConnection
//    public static ElasticsearchContainer elasticsearch
//            = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:7.17.9")
//            .withEnv("discovery.type", "single-node");
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        elasticsearch.getEnvMap().remove("xpack.security.enabled");
//        elasticsearch.setWaitStrategy(
//                new HttpWaitStrategy()
//                        .forPort(9200)
//                        .forStatusCode(200)
//                        .withStartupTimeout(Duration.ofMinutes(1)));
//
//        Startables.deepStart(postgres, elasticsearch).join();
//
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//    }
//
//    @Test
//    void contextLoads() {
//    }
//
//}