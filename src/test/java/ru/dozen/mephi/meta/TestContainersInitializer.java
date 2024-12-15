package ru.dozen.mephi.meta;

import static ru.dozen.mephi.meta.AbstractTest.postgresContainer;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class TestContainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgresContainer.getUsername(),
                "spring.datasource.password=" + postgresContainer.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}