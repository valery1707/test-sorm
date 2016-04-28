-- @see https://www.bro.org/sphinx/scripts/base/protocols/smtp/main.bro.html#type-SMTP::Info
CREATE TABLE smtp (
		ts                  NUMERIC(17, 6) NOT NULL
		COMMENT 'Время начала: unix timestamp. Этого размера хватит до 5000 года',
		uid                 VARCHAR(20)    NOT NULL
		COMMENT 'Иникальный идентификатор Bro. Вроде имеет длинну от 15 до 18, но кто его знает',
		id_orig_h           VARCHAR(45)    NOT NULL
		COMMENT 'Источник: IP[4-6]',
		id_orig_h_ip        DECIMAL(45) COMMENT 'Источник: IP[4-6] в числовом представлении',
		id_orig_p           INT            NOT NULL
		COMMENT 'Источник: порт',
		id_resp_h           VARCHAR(45)    NOT NULL
		COMMENT 'Получатель: IP[4-6]',
		id_resp_h_ip        DECIMAL(45) COMMENT 'Получатель: IP[4-6] в числовом представлении',
		id_resp_p           INT            NOT NULL
		COMMENT 'Получатель: порт',
		trans_depth         INT            NOT NULL
		COMMENT 'Represents the pipelined depth into the connection of this request/response transaction',
		helo                LONGTEXT COMMENT 'Сообщение `Helo` при установлении коннекта с сервером',
		mailfrom            LONGTEXT COMMENT 'Отправитель из заголовка `From`',
		rcptto              LONGTEXT COMMENT 'Набор получателей из заголовка `Rcpt`',
		date                LONGTEXT COMMENT 'Дата из заголовка `Date`',
		`from`              LONGTEXT COMMENT 'Отправитель из заголовка `From`',
		`to`                LONGTEXT COMMENT 'Набор получателей из заголовка `To`',
		reply_to            LONGTEXT COMMENT 'Заголовок `ReplyTo`',
		msg_id              LONGTEXT COMMENT 'Заголовок `MsgID`',
		in_reply_to         LONGTEXT COMMENT 'Заголовок `In-Reply-To`',
		subject             LONGTEXT COMMENT 'Текст письма из заголовка `Subject`',
		x_originating_ip    VARCHAR(45) COMMENT 'IP адрес отправителя из заголовка `X-Originating-IP`',
		x_originating_ip_ip DECIMAL(45) COMMENT 'IP адрес отправителя: IP[4-6] в числовом представлении',
		first_received      LONGTEXT COMMENT 'Первый полученный заголовок',
		second_received     LONGTEXT COMMENT 'Второй полученный заголовок',
		last_reply          LONGTEXT COMMENT 'Последнее сообщение от сервера к клиенту',
		path                LONGTEXT COMMENT 'Список IP адресов из маршрута письма',
		user_agent          LONGTEXT COMMENT 'Заголовок `User-Agent`',
		tls                 BOOLEAN COMMENT 'Подключение было переключено на TLS',
		fuids               LONGTEXT COMMENT 'Список UID файлов из письма',
		is_webmail          BOOLEAN COMMENT 'Письмо было отправлено через веб-почту'
)
		COMMENT = 'SMTP-трафик';
