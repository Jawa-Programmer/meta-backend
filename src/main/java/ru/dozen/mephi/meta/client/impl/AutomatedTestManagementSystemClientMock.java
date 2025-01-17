package ru.dozen.mephi.meta.client.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import ru.dozen.mephi.meta.client.AutomatedTestManagementSystemClient;
import ru.dozen.mephi.meta.client.model.TestStatus;
import ru.dozen.mephi.meta.client.model.TestStatusResponseDTO;
import ru.dozen.mephi.meta.client.model.TestType;

@Component
@ConditionalOnMissingBean(AutomatedTestManagementSystemClientImpl.class)
public class AutomatedTestManagementSystemClientMock implements AutomatedTestManagementSystemClient {


    private TestStatus generate(long taskId) {
        var vals = TestStatus.values();
        return vals[(int) (taskId % vals.length)];
    }

    @Override
    public TestStatusResponseDTO getTaskTestStatus(long taskId) {
        return TestStatusResponseDTO.builder()
                .taskId(taskId)
                .status(generate(taskId))
                .testType(TestType.TEST_CASE)
                .build();
    }

}
