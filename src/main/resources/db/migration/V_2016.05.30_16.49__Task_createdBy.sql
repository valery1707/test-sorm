ALTER TABLE task
ADD COLUMN created_by_id BIGINT
COMMENT 'Автор задания'
AFTER id;
ALTER TABLE task ADD CONSTRAINT fk$task$account_id$created_by FOREIGN KEY (created_by_id) REFERENCES account (id);

UPDATE task
SET created_by_id = (
		SELECT id
		FROM account
		WHERE username = 'admin'
);

ALTER TABLE task
MODIFY COLUMN created_by_id BIGINT NOT NULL
COMMENT 'Автор задания';
