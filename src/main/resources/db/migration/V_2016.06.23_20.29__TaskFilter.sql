CREATE TABLE task_filter (
		id      BIGINT      NOT NULL  AUTO_INCREMENT,
		task_id BIGINT      NOT NULL
		COMMENT 'Задание на перехват',
		name    VARCHAR(50) NOT NULL
		COMMENT 'Имя фильтра',
		CONSTRAINT pk$task_filter PRIMARY KEY (id),
		CONSTRAINT fk$task_filter$task$task FOREIGN KEY (task_id) REFERENCES task (id)
)
		COMMENT = 'Фильтр для задания на перехват';

CREATE TABLE task_filter_value (
		id             BIGINT        NOT NULL  AUTO_INCREMENT,
		task_filter_id BIGINT        NOT NULL
		COMMENT 'Фильтр для задания на перехват',
		value          VARCHAR(1024) NOT NULL
		COMMENT 'Значение фильтра',
		CONSTRAINT pk$task_filter_value PRIMARY KEY (id),
		CONSTRAINT fk$task_filter_value$task_filter$task_filter FOREIGN KEY (task_filter_id) REFERENCES task_filter (id)
)
		COMMENT = 'Значение фильтра для задания на перехват';
