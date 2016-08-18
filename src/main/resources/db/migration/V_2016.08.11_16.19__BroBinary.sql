CREATE TABLE bro_binary (
		ts             NUMERIC(17, 6) NOT NULL
		COMMENT 'Время начала: unix timestamp. Этого размера хватит до 5000 года',

		uid            VARCHAR(20)    NOT NULL
		COMMENT 'Иникальный идентификатор Bro. Вроде имеет длинну от 15 до 18, но кто его знает',

		id_orig_h      VARCHAR(45)    NOT NULL
		COMMENT 'Источник: IP[4-6]',
		id_orig_h_ip   DECIMAL(45)
		COMMENT 'Источник: IP[4-6] в числовом представлении',
		id_orig_p      INT(11)        NOT NULL
		COMMENT 'Источник: порт',

		id_resp_h      VARCHAR(45)    NOT NULL
		COMMENT 'Получатель: IP[4-6]',
		id_resp_h_ip   DECIMAL(45)
		COMMENT 'Получатель: IP[4-6] в числовом представлении',
		id_resp_p      INT(11)        NOT NULL
		COMMENT 'Получатель: порт',

		protocol       VARCHAR(256)   NOT NULL
		COMMENT 'Протокол',

		amt_tasks_list VARCHAR(1024)
		COMMENT 'Привязка к задачам внутри системы',

		created_at     TIMESTAMP      NOT NULL DEFAULT NOW()
		COMMENT 'Момент создания записи, для переноса в оперативную БД',
		created_by_id  BIGINT(20)
		COMMENT 'Сервер, на котором была создана запись',

		CONSTRAINT fk$bro_binary$server$created_by FOREIGN KEY (created_by_id) REFERENCES server (id),
		INDEX idx$bro_binary$created_at (created_at),
		INDEX idx$bro_binary$created_by (created_by_id)
)
		COMMENT = 'Бинарные протоколы';
