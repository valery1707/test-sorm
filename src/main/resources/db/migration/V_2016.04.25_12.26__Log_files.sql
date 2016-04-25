-- @see https://www.bro.org/sphinx/scripts/base/frameworks/files/main.bro.html#type-Files::Info
CREATE TABLE files (
		ts             NUMERIC(17, 6) NOT NULL
		COMMENT 'Время начала: unix timestamp. Этого размера хватит до 5000 года',
		fuid           VARCHAR(20)    NOT NULL
		COMMENT 'Иникальный идентификатор Bro. Вроде имеет длинну от 15 до 18, но кто его знает',
		tx_hosts       LONGTEXT COMMENT 'Отправитель: набор IP-адресов',
		rx_hosts       LONGTEXT COMMENT 'Получатель: набор IP-адресов',
		conn_uids      LONGTEXT COMMENT 'Набор идентификаторов коннектов. @see conn',
		source         LONGTEXT COMMENT 'Идентификатор источника файла: сетевой протокол, путь до прочитанного файла, что-нибудь ещё',
		depth          INT COMMENT 'Глубина вложенности файла в рамках коннекта. SMTP - номер вложения, и т.п.',
		analyzers      VARCHAR(256) COMMENT 'Сет применённых анализаторов (хеширование, извлечение, и т.п.)',
		mime_type      VARCHAR(256) COMMENT 'Предположительный mime-тип для файла угаданных по содержимому',
		filename       LONGTEXT COMMENT 'Возможное имя файла, определенное из источника. Чаще всего получается из заголовка `Content-Disposition`',
		duration       NUMERIC(13, 6) COMMENT 'Продолжительность анализа файла?',
		local_orig     BOOLEAN COMMENT 'Если источник файла сетевое соединение, то находится ли источник в локальной сети',
		is_orig        BOOLEAN COMMENT 'Если источник файла сетевое соединение, то описывает кто отправил файл - источник соединения или его получатель',
		seen_bytes     INT COMMENT 'Количество байт переданных на анализ',
		total_bytes    INT COMMENT 'Полный размер файла в байтах',
		missing_bytes  INT COMMENT 'Количество потерянных байт',
		overflow_bytes INT COMMENT 'Количество байт не попавших в анализатор.',
		timedout       BOOLEAN COMMENT 'Был ли обнаружен таймаут при аналице файла',
		parent_fuid    VARCHAR(20) COMMENT 'Ссылка на файл-контейнер, из которого был извлечён даннвй файл для анализа',
		md5            VARCHAR(32) COMMENT 'MD5',
		sha1           VARCHAR(40) COMMENT 'SHA1',
		sha256         VARCHAR(64) COMMENT 'SHA256',
		extracted      LONGTEXT COMMENT 'Имя извлечённого файла в директории'
)
		COMMENT = 'Данные по файлам';
