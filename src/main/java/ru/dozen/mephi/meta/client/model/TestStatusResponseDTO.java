package ru.dozen.mephi.meta.client.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestStatusResponseDTO implements Serializable {

    private Long taskId;
    private TestType testType;
    private TestStatus status;
}
