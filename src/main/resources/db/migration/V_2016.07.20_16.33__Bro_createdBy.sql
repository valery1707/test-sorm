ALTER TABLE bro_conn ADD COLUMN created_by_id BIGINT COMMENT 'Сервер, на котором была создана запись' REFERENCES server(id);
ALTER TABLE bro_files ADD COLUMN created_by_id BIGINT COMMENT 'Сервер, на котором была создана запись' REFERENCES server(id);
ALTER TABLE bro_smtp ADD COLUMN created_by_id BIGINT COMMENT 'Сервер, на котором была создана запись' REFERENCES server(id);
ALTER TABLE bro_http ADD COLUMN created_by_id BIGINT COMMENT 'Сервер, на котором была создана запись' REFERENCES server(id);
