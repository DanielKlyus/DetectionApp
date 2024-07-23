CREATE TABLE users (
	"id"		    BIGSERIAL 	PRIMARY KEY	NOT NULL,
	"name"		    TEXT		NOT NULL,
	"is_admin"      BOOLEAN		NOT NULL
);

CREATE TABLE projects (
	"id" 		    BIGSERIAL	PRIMARY KEY NOT NULL,
	"name"		    TEXT		NOT NULL,
	"path_source"   TEXT        NOT NULL,
	"path_save"     TEXT        NOT NULL,
	"user_id"	    BIGINT		NOT NULL,
    "is_active"     BOOLEAN     NOT NULL,
    "result"        JSONB,
	CONSTRAINT fk_projects_user FOREIGN KEY ("user_id") REFERENCES users ("id")
);

CREATE TABLE categories (
    "id"            BIGSERIAL   PRIMARY KEY NOT NULL,
    "name"		    TEXT		NOT NULL,
    "species"       TEXT        NOT NULL,
    "img"           TEXT        NOT NULL,
    "class"         TEXT        NOT NULL DEFAULT 'animal',
    "type"          TEXT        NOT NULL
);

CREATE TABLE images (
    "id"            BIGSERIAL   PRIMARY KEY NOT NULL,
    "name"		    TEXT		NOT NULL,
    "path"          TEXT        NOT NULL,
    "project_id"    BIGINT      NOT NULL,
    "minio_url"     TEXT        NOT NULL,
    CONSTRAINT fk_images_project FOREIGN KEY ("project_id") REFERENCES projects ("id")
)