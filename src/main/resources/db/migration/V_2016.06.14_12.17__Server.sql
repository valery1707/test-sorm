CREATE TABLE server (
		id             BIGINT        NOT NULL  AUTO_INCREMENT,
		created_by_id  BIGINT        NOT NULL
		COMMENT 'Пользователь, создавший запись',
		created_at     DATETIME      NOT NULL
		COMMENT 'Время создания записи',
		modified_by_id BIGINT        NOT NULL
		COMMENT 'Пользователь, создавший запись',
		modified_at    DATETIME      NOT NULL
		COMMENT 'Время создания записи',
		host           VARCHAR(512)  NOT NULL
		COMMENT 'Host name or IP address',
		port           INTEGER       NOT NULL
		COMMENT 'Порт',
		username       VARCHAR(1024) NOT NULL
		COMMENT 'Имя пользователя',
		password       VARCHAR(1024) NOT NULL
		COMMENT 'Пароль',
		bro_path       VARCHAR(1024) NOT NULL
		COMMENT 'Путь до усттановки Bro',
		conf_path      VARCHAR(1024) NOT NULL
		COMMENT 'Путь до расположения наших конфигов',
		CONSTRAINT pk$server PRIMARY KEY (id),
		CONSTRAINT fk$server$account$created_by FOREIGN KEY (created_by_id) REFERENCES account (id),
		CONSTRAINT fk$server$account$modified_by FOREIGN KEY (modified_by_id) REFERENCES account (id)
)
		COMMENT = 'Сервера Bro';