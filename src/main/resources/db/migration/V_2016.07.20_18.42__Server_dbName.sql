ALTER TABLE server ADD COLUMN db_name VARCHAR(100) NOT NULL DEFAULT 'amt_karaganda' COMMENT 'Имя БД откуда брать данные для переноса в оперативную БД';
ALTER TABLE server ALTER COLUMN db_name DROP DEFAULT;
