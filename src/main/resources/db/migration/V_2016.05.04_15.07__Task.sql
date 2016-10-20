CREATE TABLE task (
		id            BIGINT        NOT NULL AUTO_INCREMENT,
		created_at    DATETIME      NOT NULL,
		agency        VARCHAR(1024) NOT NULL,
		client_alias  VARCHAR(1024) NOT NULL,
		period_start  DATETIME      NOT NULL,
		period_finish DATETIME      NOT NULL,
		note          VARCHAR(1024),
		CONSTRAINT pk$task PRIMARY KEY (id)
)
		COMMENT = '';
