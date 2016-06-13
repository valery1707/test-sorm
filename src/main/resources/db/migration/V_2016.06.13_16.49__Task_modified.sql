ALTER TABLE task
ADD COLUMN modified_by_id BIGINT
COMMENT 'Автор последнего изменения'
AFTER created_at;
ALTER TABLE task ADD CONSTRAINT fk$task$account$modified_by FOREIGN KEY (modified_by_id) REFERENCES account (id);

ALTER TABLE task
ADD COLUMN modified_at TIMESTAMP
COMMENT 'Время последнего изменения'
AFTER modified_by_id;

UPDATE task
SET modified_by_id = created_by_id, modified_at = created_at;

ALTER TABLE task
MODIFY COLUMN modified_by_id BIGINT NOT NULL
COMMENT 'Автор последнего изменения';

ALTER TABLE task
MODIFY COLUMN modified_at TIMESTAMP NOT NULL
COMMENT 'Время последнего изменения';
