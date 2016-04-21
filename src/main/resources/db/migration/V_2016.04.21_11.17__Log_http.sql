-- @see https://www.bro.org/sphinx/scripts/base/protocols/http/main.bro.html#type-HTTP::Info
CREATE TABLE http (
		ts                NUMERIC(17, 6) NOT NULL
		COMMENT 'Время начала: unix timestamp. Этого размера хватит до 5000 года',
		uid               VARCHAR(20)    NOT NULL
		COMMENT 'Иникальный идентификатор Bro. Вроде имеет длинну от 15 до 18, но кто его знает',
		id_orig_h         VARCHAR(45)    NOT NULL
		COMMENT 'Источник: IP[4-6]',
		id_orig_p         INT            NOT NULL
		COMMENT 'Источник: порт',
		id_resp_h         VARCHAR(45)    NOT NULL
		COMMENT 'Получатель: IP[4-6]',
		id_resp_p         INT            NOT NULL
		COMMENT 'Получатель: порт',
		trans_depth       INT            NOT NULL
		COMMENT 'Represents the pipelined depth into the connection of this request/response transaction',
		method            VARCHAR(10) COMMENT 'HTTP-метод (HEAD, GET, POST, PUT, ...)',
		host              LONGTEXT COMMENT 'Значение заголовка `HOST`',
		uri               LONGTEXT COMMENT 'URI',
		referrer          LONGTEXT COMMENT 'Значение заголовка `REFERER`',
		user_agent        LONGTEXT COMMENT 'Значение заголовка `User-Agent`',
		request_body_len  INT COMMENT 'Реальный размер несжатого содержимого переданного с клента',
		response_body_len INT COMMENT 'Реальный размер несжатого содержимого переданного с сервера',
		status_code       INT COMMENT 'Статус обработки запроса: код',
		status_msg        LONGTEXT COMMENT 'Статус обработки запроса: сообщение',
		info_code         INT COMMENT 'Последний замеченный ответ 1xx: код',
		info_msg          LONGTEXT COMMENT 'Последний замеченный ответ 1xx: сообщение',
		filename          LONGTEXT COMMENT 'Имя файла из заголовка `Content-Disposition`',
		tags              VARCHAR(200) COMMENT 'Теги дополнительных анализаторов HTTP-трафика',
		username          LONGTEXT COMMENT 'Basic-авторизация: логин',
		password          LONGTEXT COMMENT 'Basic-авторизация: пароль',
		proxied           LONGTEXT COMMENT 'Все заголовки которые могут означать что запрос проксируется',
		orig_fuids        LONGTEXT COMMENT 'Отправлено: список UID всех файлов',
		orig_mime_types   LONGTEXT COMMENT 'Отправлено: MIME-типы всех файлов',
		resp_fuids        LONGTEXT COMMENT 'Получено: список UID всех файлов',
		resp_mime_types   LONGTEXT COMMENT 'Получено: MIME-типы всех файлов'
)
		COMMENT = 'HTTP-трафик';
