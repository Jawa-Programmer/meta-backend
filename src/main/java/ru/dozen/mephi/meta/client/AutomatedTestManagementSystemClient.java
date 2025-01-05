package ru.dozen.mephi.meta.client;

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
    String getTaskTestStatus(long taskId);
}
