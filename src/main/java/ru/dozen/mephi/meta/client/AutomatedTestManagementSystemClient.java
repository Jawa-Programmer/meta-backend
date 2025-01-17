package ru.dozen.mephi.meta.client;

import ru.dozen.mephi.meta.client.model.TestStatusResponseDTO;

/**
 * Клиент взаимодействия с внешней АСУТ
 */
public interface AutomatedTestManagementSystemClient {

    /**
     * Метод запрашивает статус задачи у внешней системы по идентификатору задачи
     *
     * @param taskId идентификатор задачи
     * @return статус тестирования задачи
     */
    TestStatusResponseDTO getTaskTestStatus(long taskId);
}
