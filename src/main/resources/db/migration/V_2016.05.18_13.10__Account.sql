CREATE TABLE account (
		id           BIGINT        NOT NULL AUTO_INCREMENT,
		username     VARCHAR(50)   NOT NULL
		COMMENT 'Имя пользователя',
		password     VARCHAR(100)  NOT NULL
		COMMENT 'Пароль. BCrypt-шифрование с рамдомной солью',
		is_active    BOOL          NOT NULL
		COMMENT 'Признак активности',
		role         VARCHAR(10)   NOT NULL
		COMMENT 'Роль: SUPER, ADMIN, OPERATOR, SUPERVISOR',
		active_until DATE
		COMMENT 'Срок действия',
		agency       VARCHAR(1024) NOT NULL
		COMMENT 'Наименование органа, осуществляющего проведение ОРМ или надзор',
		CONSTRAINT pk$account PRIMARY KEY (id)
)
		COMMENT = 'Пользователи системы';

-- Password: 1q2w3e4r
INSERT INTO account (username, password, is_active, role, active_until, agency)
VALUES ('admin', '$2a$10$L0nYZKA.mJy0ky6V2dmBKudnGK4d4p6ggpbQcq3XZTshV7R2xywdi', TRUE, 'SUPER', NULL, 'AstanaMegaTel');
