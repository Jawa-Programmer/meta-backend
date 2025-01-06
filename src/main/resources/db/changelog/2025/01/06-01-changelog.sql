-- liquibase formatted sql

-- changeset Siblion:1736162882952-1
ALTER TABLE tasks
    ALTER COLUMN project_id SET NOT NULL;

