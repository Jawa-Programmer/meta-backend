package ru.dozen.mephi.meta;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainerProvider;

public class TestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final JdbcDatabaseContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainerProvider()
            .newInstance("15.3")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        POSTGRES_CONTAINER.start();

        TestPropertyValues.of(
                "spring.datasource.url=" + POSTGRES_CONTAINER.getJdbcUrl(),
                "spring.datasource.username=" + POSTGRES_CONTAINER.getUsername(),
                "spring.datasource.password=" + POSTGRES_CONTAINER.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}