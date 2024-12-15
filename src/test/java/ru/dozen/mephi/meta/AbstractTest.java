package ru.dozen.mephi.meta;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = TestContainersInitializer.class)
class AbstractTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Container
    public static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");


}
