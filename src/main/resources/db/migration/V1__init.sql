CREATE TABLE users (
	"id"		    serial		PRIMARY KEY	NOT NULL,
	"name"		    TEXT		NOT NULL,
	"is_admin"      BOOLEAN		NOT NULL
);

CREATE TABLE projects (
	"id" 		    serial		PRIMARY KEY NOT NULL,
	"name"		    TEXT		NOT NULL,
	"path_source"   TEXT        NOT NULL,
	"path_save"     TEXT        NOT NULL,
	"user_id"	    INTEGER		NOT NULL,

	CONSTRAINT fk_projects_user FOREIGN KEY ("user_id") REFERENCES users ("id")
);