CREATE TABLE IF NOT EXISTS users
(
    "id"       BIGSERIAL PRIMARY KEY NOT NULL,
    "name"     TEXT                  NOT NULL,
    "is_admin" BOOLEAN               NOT NULL
);

CREATE TABLE IF NOT EXISTS projects
(
    "id"          BIGSERIAL PRIMARY KEY NOT NULL,
    "name"        TEXT                  NOT NULL,
    "path_source" TEXT                  NOT NULL,
    "path_save"   TEXT                  NOT NULL,
    "user_id"     BIGINT                NOT NULL,
    "result"      JSONB,
    CONSTRAINT fk_projects_user FOREIGN KEY ("user_id") REFERENCES users ("id")
);

CREATE TABLE IF NOT EXISTS categories
(
    "id"         BIGSERIAL PRIMARY KEY NOT NULL,
    "name"       TEXT                  NOT NULL,
    "species"    TEXT                  NOT NULL,
    "img"        TEXT                  NOT NULL,
    "class"      TEXT                  NOT NULL DEFAULT 'animal',
    "type"       TEXT                  NOT NULL,
    "project_id" BIGINT                NOT NULL,
    CONSTRAINT fk_categories_project FOREIGN KEY ("project_id") REFERENCES projects ("id")
);

CREATE TABLE IF NOT EXISTS images
(
    "id"                      BIGSERIAL PRIMARY KEY NOT NULL,
    "name"                    TEXT                  NOT NULL,
    "path"                    TEXT                  NOT NULL,
    "category_id"             BIGINT                NOT NULL,
    "minio_url"               TEXT                  NOT NULL,
    "datetime"                TIMESTAMP,
    "animal_count"            INTEGER               NOT NULL,
    "passage"                 INTEGER               NOT NULL DEFAULT 0,
    "animal_count_in_passage" INTEGER               NOT NULL DEFAULT 0,
    "threshold"               REAL                  NOT NULL,
    CONSTRAINT fk_images_project FOREIGN KEY ("category_id") REFERENCES categories ("id")
);
