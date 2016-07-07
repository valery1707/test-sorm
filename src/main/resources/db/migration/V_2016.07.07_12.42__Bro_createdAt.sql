ALTER TABLE bro_conn ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW() COMMENT 'Момент создания записи, для переноса в оперативную БД';
ALTER TABLE bro_files ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW() COMMENT 'Момент создания записи, для переноса в оперативную БД';
ALTER TABLE bro_smtp ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW() COMMENT 'Момент создания записи, для переноса в оперативную БД';
ALTER TABLE bro_http ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW() COMMENT 'Момент создания записи, для переноса в оперативную БД';
