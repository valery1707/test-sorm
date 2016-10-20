CREATE TABLE event (
		id            BIGINT      NOT NULL AUTO_INCREMENT,

		created_by_id BIGINT
		COMMENT 'Пользователь к которому относится событие. Может быть NULL если событие системное',

		created_at    DATETIME    NOT NULL
		COMMENT 'Момент создания',

		type          VARCHAR(30) NOT NULL
		COMMENT 'Тип (enum)',

		success       BOOLEAN     NOT NULL
		COMMENT 'Успешность события',

		ip            VARCHAR(45) NOT NULL
		COMMENT 'IP-адрес клиента',

		session_id    BIGINT
		COMMENT 'Ссылка на сессию работы',

		ext           LONGTEXT    NOT NULL
		COMMENT 'Дополнительные данные',

		CONSTRAINT pk$event PRIMARY KEY (id),
		CONSTRAINT fk$event$account$account FOREIGN KEY (created_by_id) REFERENCES account (id),
		CONSTRAINT fk$event$account_session$session FOREIGN KEY (session_id) REFERENCES account_session (id)
)
		COMMENT = 'Журнал событий';
