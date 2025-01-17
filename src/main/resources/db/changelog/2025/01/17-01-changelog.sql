-- liquibase formatted sql

ALTER TABLE task_watchers
    ADD CONSTRAINT uk_user_in_task UNIQUE (task_id, user_id);

