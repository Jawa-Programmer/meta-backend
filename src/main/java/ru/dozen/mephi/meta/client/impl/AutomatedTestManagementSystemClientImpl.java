package ru.dozen.mephi.meta.client.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.dozen.mephi.meta.client.AutomatedTestManagementSystemClient;
import ru.dozen.mephi.meta.config.ATMSClientProperties;

@Component
@RequiredArgsConstructor
public class AutomatedTestManagementSystemClientImpl implements AutomatedTestManagementSystemClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ATMSClientProperties properties;

    @Override
    public String getTaskTestStatus(long taskId) {
        return "";
//        var rq = RequestEntity.get(properties.getGetTaskTestStatusEndpoint())
//                .header("Authorization", properties.getAuthToken())
//                .build();
//        return restTemplate.exchange(rq, String.class).getBody();
    }
}
