-- liquibase formatted sql

-- changeset Siblion:1736108741586-1
CREATE SEQUENCE IF NOT EXISTS comments_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1736108741586-2
CREATE SEQUENCE IF NOT EXISTS projects_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1736108741586-3
CREATE SEQUENCE IF NOT EXISTS role_records_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1736108741586-4
CREATE SEQUENCE IF NOT EXISTS tasks_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1736108741586-5
CREATE SEQUENCE IF NOT EXISTS user_roles_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1736108741586-6
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

-- changeset Siblion:1736108741586-7
CREATE TABLE comments
(
    id        BIGINT NOT NULL,
    author_id BIGINT,
    task_id   BIGINT,
    text      TEXT,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);

-- changeset Siblion:1736108741586-8
CREATE TABLE projects
(
    id          BIGINT NOT NULL,
    title       VARCHAR(255),
    state       VARCHAR(255),
    director_id BIGINT,
    CONSTRAINT pk_projects PRIMARY KEY (id)
);

-- changeset Siblion:1736108741586-9
CREATE TABLE role_records
(
    id         BIGINT NOT NULL,
    role_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    CONSTRAINT pk_role_records PRIMARY KEY (id)
);

-- changeset Siblion:1736108741586-10
CREATE TABLE task_watchers
(
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL
);

-- changeset Siblion:1736108741586-11
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

-- changeset Siblion:1736108741586-12
CREATE TABLE user_roles
(
    id               BIGINT       NOT NULL,
    role_name        VARCHAR(255) NOT NULL,
    role_description VARCHAR(255),
    CONSTRAINT pk_user_roles PRIMARY KEY (id)
);

-- changeset Siblion:1736108741586-13
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

-- changeset Siblion:1736108741586-14
ALTER TABLE tasks
    ADD CONSTRAINT uc_08ad7dd0e8edaf94453c708e7 UNIQUE (key, project_id);

-- changeset Siblion:1736108741586-15
ALTER TABLE role_records
    ADD CONSTRAINT uc_1985d32d8ce6015623cc17b4b UNIQUE (user_id, role_id, project_id);

-- changeset Siblion:1736108741586-16
ALTER TABLE user_roles
    ADD CONSTRAINT uc_user_roles_role_name UNIQUE (role_name);

-- changeset Siblion:1736108741586-17
ALTER TABLE users
    ADD CONSTRAINT uc_users_login UNIQUE (login);

-- changeset Siblion:1736108741586-18
ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

-- changeset Siblion:1736108741586-19
ALTER TABLE comments
    ADD CONSTRAINT FK_COMMENTS_ON_TASK FOREIGN KEY (task_id) REFERENCES tasks (id);

-- changeset Siblion:1736108741586-20
ALTER TABLE projects
    ADD CONSTRAINT FK_PROJECTS_ON_DIRECTOR FOREIGN KEY (director_id) REFERENCES users (id);

-- changeset Siblion:1736108741586-21
ALTER TABLE role_records
    ADD CONSTRAINT FK_ROLE_RECORDS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

-- changeset Siblion:1736108741586-22
ALTER TABLE role_records
    ADD CONSTRAINT FK_ROLE_RECORDS_ON_ROLE FOREIGN KEY (role_id) REFERENCES user_roles (id);

-- changeset Siblion:1736108741586-23
ALTER TABLE role_records
    ADD CONSTRAINT FK_ROLE_RECORDS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset Siblion:1736108741586-24
ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_AUTHOR FOREIGN KEY (author_id) REFERENCES users (id);

-- changeset Siblion:1736108741586-25
ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_EXECUTOR FOREIGN KEY (executor_id) REFERENCES users (id);

-- changeset Siblion:1736108741586-26
ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_PROJECT FOREIGN KEY (project_id) REFERENCES projects (id);

-- changeset Siblion:1736108741586-27
ALTER TABLE task_watchers
    ADD CONSTRAINT fk_taswat_on_task FOREIGN KEY (task_id) REFERENCES tasks (id);

-- changeset Siblion:1736108741586-28
ALTER TABLE task_watchers
    ADD CONSTRAINT fk_taswat_on_user FOREIGN KEY (user_id) REFERENCES users (id);

create function try_cast_int(p_in text, p_default int default null)
    returns int
as
$$
begin
    return $1::int;
exception
    when others then
        return p_default;
end
$$ language plpgsql;