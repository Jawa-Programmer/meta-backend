package ru.dozen.mephi.meta;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.dozen.mephi.meta.config.ATMSClientProperties;

@SpringBootApplication
@EnableConfigurationProperties({ATMSClientProperties.class})
@OpenAPIDefinition(
        info = @Info(description = "MEPhI Tasks")
)
public class MetaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetaApplication.class, args);
    }

}
