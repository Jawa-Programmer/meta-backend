-- liquibase formatted sql

-- changeset Siblion:1734337786456-1
CREATE SEQUENCE IF NOT EXISTS comments_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1734337786456-2
CREATE SEQUENCE IF NOT EXISTS projects_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1734337786456-3
CREATE SEQUENCE IF NOT EXISTS role_records_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1734337786456-4
CREATE SEQUENCE IF NOT EXISTS tasks_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1734337786456-5
CREATE SEQUENCE IF NOT EXISTS user_roles_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1734337786456-6
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1734337786456-7
CREATE TABLE comments
(
    id        BIGINT NOT NULL,
    author_id BIGINT,
    task_id   BIGINT,
    text      TEXT,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

-- changeset Siblion:1734337786456-8
CREATE TABLE projects
(
    id          BIGINT NOT NULL,
    title       VARCHAR(255),
    state       VARCHAR(255),
    director_id BIGINT,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

-- changeset Siblion:1734337786456-9
CREATE TABLE role_records
(
    id         BIGINT NOT NULL,
    role_id    BIGINT,
    user_id    BIGINT,
    project_id BIGINT,
    CONSTRAINT pk_role_records PRIMARY KEY (id)
);

-- changeset Siblion:1734337786456-10
CREATE TABLE task_watchers
(
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL
);

-- changeset Siblion:1734337786456-11
CREATE TABLE tasks
(
    id                  BIGINT NOT NULL,
    key                 VARCHAR(10),
    title               VARCHAR(255),
    description         TEXT,
    is_testing_required BOOLEAN,
    author_id           BIGINT,
    executor_id         BIGINT,
    project_id          BIGINT,
    state               VARCHAR(255),
    CONSTRAINT pk_tasks PRIMARY KEY (id)
);

-- changeset Siblion:1734337786456-12
CREATE TABLE user_roles
(
    id        BIGINT NOT NULL,
    role_name VARCHAR(255),
    CONSTRAINT pk_user_roles PRIMARY KEY (id)
);

-- changeset Siblion:1734337786456-13
CREATE TABLE users
(
    id           BIGINT       NOT NULL,
    login        VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    fio          VARCHAR(255),
    user_state   VARCHAR(255) NOT NULL,
    picture_path VARCHAR(255),
    system_role  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset Siblion:1734337786456-14
ALTER TABLE users
    ADD CONSTRAINT uc_users_login UNIQUE (login);

-- changeset Siblion:1734337786456-15
ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

-- changeset Siblion:1734337786456-16
ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_TASK FOREIGN KEY (task_id) REFERENCES tasks (id);

-- changeset Siblion:1734337786456-17
ALTER TABLE projects
    ADD CONSTRAINT FK_PROJECTS_ON_DIRECTOR FOREIGN KEY (director_id) REFERENCES users (id);

-- changeset Siblion:1734337786456-18
ALTER TABLE role_records
    ADD CONSTRAINT FK_ROLE_RECORDS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

-- changeset Siblion:1734337786456-19
ALTER TABLE role_records
    ADD CONSTRAINT FK_ROLE_RECORDS_ON_ROLE FOREIGN KEY (role_id) REFERENCES user_roles (id);

-- changeset Siblion:1734337786456-20
ALTER TABLE role_records
    ADD CONSTRAINT FK_ROLE_RECORDS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset Siblion:1734337786456-21
ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

-- changeset Siblion:1734337786456-22
ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_EXECUTOR FOREIGN KEY (executor_id) REFERENCES users (id);

-- changeset Siblion:1734337786456-23
ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

-- changeset Siblion:1734337786456-24
ALTER TABLE task_watchers
    ADD CONSTRAINT fk_taswat_on_task FOREIGN KEY (task_id) REFERENCES tasks (id);

-- changeset Siblion:1734337786456-25
ALTER TABLE task_watchers
    ADD CONSTRAINT fk_taswat_on_user FOREIGN KEY (user_id) REFERENCES users (id);

