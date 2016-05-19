ALTER TABLE account_session
ADD COLUMN details LONGTEXT
COMMENT 'Расширенные данные по коннекту. Формат: JSON'
AFTER session_id;

UPDATE account_session
SET details = '{}';

ALTER TABLE account_session
MODIFY COLUMN details LONGTEXT NOT NULL
COMMENT 'Расширенные данные по коннекту. Формат: JSON';
