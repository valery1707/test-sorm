CREATE TABLE account_session (
		id         BIGINT      NOT NULL AUTO_INCREMENT,
		account_id BIGINT      NOT NULL
		COMMENT 'Пользователь',
		login_at   DATETIME    NOT NULL
		COMMENT 'Время входа',
		login_as   VARCHAR(50) NOT NULL
		COMMENT 'Режим входа: MANUAL, INTERACTIVE, REMEMBER_ME',
		session_id VARCHAR(50) NOT NULL
		COMMENT 'Внутренний ID сессии',
		logout_at  DATETIME
		COMMENT 'Время выхода',
		logout_as  VARCHAR(50)
		COMMENT 'Режим выхода: MANUAL, TIMEOUT',
		CONSTRAINT pk$account_session PRIMARY KEY (id)
)
		COMMENT = 'Сессии работы пользователей системы';
