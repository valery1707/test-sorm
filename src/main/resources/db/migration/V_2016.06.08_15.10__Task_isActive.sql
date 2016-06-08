ALTER TABLE task
ADD COLUMN is_active BOOLEAN
COMMENT 'Задание активно'
AFTER id;

UPDATE task
SET is_active = TRUE;

ALTER TABLE task
MODIFY COLUMN is_active BOOLEAN NOT NULL
COMMENT 'Задание активно';
