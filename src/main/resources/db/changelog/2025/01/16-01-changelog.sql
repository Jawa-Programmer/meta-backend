-- liquibase formatted sql

-- changeset Siblion:1737029489062-1
ALTER TABLE tasks
    ALTER COLUMN key SET NOT NULL;

