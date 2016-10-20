CREATE TABLE server_status (
		id          BIGINT   NOT NULL AUTO_INCREMENT,
		server_id   BIGINT
		COMMENT 'Сервер',
		modified_at DATETIME NOT NULL
		COMMENT 'Время изменения записи',
		host_status BOOLEAN  NOT NULL
		COMMENT 'Статус хоста',
		db_status   BOOLEAN  NOT NULL
		COMMENT 'Статус БД',

		CONSTRAINT pk$server_status PRIMARY KEY (id),
		CONSTRAINT fk$server_status$server$server FOREIGN KEY (server_id) REFERENCES server (id)
)
		COMMENT = 'Актуальный статус мониторинга оборудования';

-- Пульт управления/Веб-сервер
INSERT INTO server_status (server_id, modified_at, host_status, db_status) VALUES (NULL, NOW(), FALSE, FALSE);

-- Отдельные сервера
INSERT INTO server_status (server_id, modified_at, host_status, db_status)
		SELECT
				id,
				NOW(),
				FALSE,
				FALSE
		FROM server;
