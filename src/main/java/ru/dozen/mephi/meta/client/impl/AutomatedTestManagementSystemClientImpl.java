package ru.dozen.mephi.meta.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.dozen.mephi.meta.client.AutomatedTestManagementSystemClient;
import ru.dozen.mephi.meta.client.model.TestStatusResponseDTO;
import ru.dozen.mephi.meta.config.ATMSClientProperties;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty("atms-client.host")
public class AutomatedTestManagementSystemClientImpl implements AutomatedTestManagementSystemClient {

    private final RestTemplate restTemplate;
    private final ATMSClientProperties properties;

    @Override
    public TestStatusResponseDTO getTaskTestStatus(long taskId) {
        var rq = RequestEntity.get(properties.getGetTaskTestStatusEndpoint(), taskId)
                .header("Authorization", "Bearer " + properties.getAuthToken())
                .build();
        return restTemplate.exchange(rq, TestStatusResponseDTO.class).getBody();
    }
}
