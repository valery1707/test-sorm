ALTER TABLE bro_conn ADD COLUMN created_by_id BIGINT COMMENT 'Сервер, на котором была создана запись';
ALTER TABLE bro_conn ADD CONSTRAINT fk$bro_conn$server$created_by FOREIGN KEY (created_by_id) REFERENCES server (id);

ALTER TABLE bro_files ADD COLUMN created_by_id BIGINT COMMENT 'Сервер, на котором была создана запись';
ALTER TABLE bro_files ADD CONSTRAINT fk$bro_files$server$created_by FOREIGN KEY (created_by_id) REFERENCES server (id);

ALTER TABLE bro_smtp ADD COLUMN created_by_id BIGINT COMMENT 'Сервер, на котором была создана запись';
ALTER TABLE bro_smtp ADD CONSTRAINT fk$bro_smtp$server$created_by FOREIGN KEY (created_by_id) REFERENCES server (id);

ALTER TABLE bro_http ADD COLUMN created_by_id BIGINT COMMENT 'Сервер, на котором была создана запись';
ALTER TABLE bro_http ADD CONSTRAINT fk$bro_http$server$created_by FOREIGN KEY (created_by_id) REFERENCES server (id);
