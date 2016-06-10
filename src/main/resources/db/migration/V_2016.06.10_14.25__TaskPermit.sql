CREATE TABLE task_permit (
		id            BIGINT        NOT NULL AUTO_INCREMENT,
		created_by_id BIGINT        NOT NULL
		COMMENT 'Пользователь, создавший запись',
		created_at    TIMESTAMP     NOT NULL
		COMMENT 'Время создания записи',
		agency        VARCHAR(1024) NOT NULL
		COMMENT 'Наименование органа, осуществляющего проведение ОРМ или надзор',
		task_id       BIGINT        NOT NULL
		COMMENT 'Задание на перехват сообщений, на которую выдана санкция',
		account_id    BIGINT        NOT NULL
		COMMENT 'Пользователь, которому выдана санкция',
		period_start  TIMESTAMP     NOT NULL
		COMMENT 'Время действия санции, начало',
		period_finish TIMESTAMP     NOT NULL
		COMMENT 'Время действия санции, конец',
		is_active     BOOLEAN       NOT NULL DEFAULT TRUE
		COMMENT 'Признак активности записи',
		CONSTRAINT pk$task_permit PRIMARY KEY (id),
		CONSTRAINT fk$task_permit$account$created_by FOREIGN KEY (created_by_id) REFERENCES account (id),
		CONSTRAINT fk$task_permit$task$task FOREIGN KEY (task_id) REFERENCES task (id),
		CONSTRAINT fk$task_permit$account$account FOREIGN KEY (account_id) REFERENCES account (id)
)
		COMMENT = 'Санкции на доступ к заданиям на перехват сообщений';