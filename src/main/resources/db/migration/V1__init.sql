CREATE TABLE users (
	"id"		    BIGINT 		PRIMARY KEY	NOT NULL,
	"name"		    TEXT		NOT NULL,
	"is_admin"      BOOLEAN		NOT NULL
);

CREATE TABLE projects (
	"id" 		    BIGINT		PRIMARY KEY NOT NULL,
	"name"		    TEXT		NOT NULL,
	"path_source"   TEXT        NOT NULL,
	"path_save"     TEXT        NOT NULL,
	"user_id"	    BIGINT		NOT NULL,
    "is_active"     BOOLEAN     NOT NULL,
	CONSTRAINT fk_projects_user FOREIGN KEY ("user_id") REFERENCES users ("id")
);