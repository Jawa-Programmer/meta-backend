package ru.dozen.mephi.meta.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "atms-client")
public class ATMSClientProperties {

    private String host;
    private String getTaskTestStatusEndpoint;
    private String authToken;
}
