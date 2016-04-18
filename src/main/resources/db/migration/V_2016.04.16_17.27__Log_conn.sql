-- @see https://www.bro.org/sphinx/scripts/base/protocols/conn/main.bro.html#type-Conn::Info
CREATE TABLE conn (
		ts             NUMERIC(17, 6) NOT NULL
		COMMENT 'Время начала: unix timestamp. Этого размера хватит до 5000 года',
		uid            VARCHAR(20)    NOT NULL
		COMMENT 'Иникальный идентификатор Bro. Вроде имеет длинну от 15 до 18, но кто его знает',
		`id.orig_h`    VARCHAR(45)    NOT NULL
		COMMENT 'Источник: IP[4-6]',
		`id.orig_p`    INT            NOT NULL
		COMMENT 'Источник: порт',
		`id.resp_h`    VARCHAR(45)    NOT NULL
		COMMENT 'Получатель: IP[4-6]',
		`id.resp_p`    INT            NOT NULL
		COMMENT 'Получатель: порт',
		proto          VARCHAR(5)     NOT NULL
		COMMENT 'Протокол: tcp/udp/icmp/dns',
		service        VARCHAR(20) COMMENT 'Сервис: dns,ftp,ftp-data,http,pop3,smtp,`ssl,smtp`',
		duration       NUMERIC(13, 6) COMMENT 'Продолжительность: unix timestamp.',
		orig_bytes     INT COMMENT 'Байт отправлено',
		resp_bytes     INT COMMENT 'Байт получено',
		conn_state     VARCHAR(20) COMMENT 'Состояние коннекта',
		local_orig     BOOLEAN COMMENT 'Источник в локальной сети',
		local_resp     BOOLEAN COMMENT 'Получатель в локальной сети',
		missed_bytes   INT COMMENT 'Количество потерянных байт',
		history        VARCHAR(20) COMMENT 'История состояния коннекта',
		orig_pkts      INT COMMENT 'Байт отправлено, более точные данные',
		orig_ip_bytes  INT COMMENT 'Байт отправлено, на уровне IP',
		resp_pkts      INT COMMENT 'Байт получено, более точные данные',
		resp_ip_bytes  INT COMMENT 'Байт получено, на уровне IP',
		tunnel_parents VARCHAR(1024) COMMENT 'Если коннект',
		amt_tasks_list VARCHAR(1024) COMMENT 'Привязка к задачам внутри системы'
)
		COMMENT = 'Общая информация о подключениях'
;
