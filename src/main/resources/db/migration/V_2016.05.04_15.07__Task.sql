CREATE TABLE task (
		id            BIGINT        NOT NULL AUTO_INCREMENT,
		created_at    TIMESTAMP     NOT NULL,
		agency        VARCHAR(1024) NOT NULL,
		client_alias  VARCHAR(1024) NOT NULL,
		period_start  TIMESTAMP     NOT NULL,
		period_finish TIMESTAMP     NOT NULL,
		note          VARCHAR(1024),
		CONSTRAINT pk$task PRIMARY KEY (id)
)
		COMMENT = '';
