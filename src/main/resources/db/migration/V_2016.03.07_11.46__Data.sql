CREATE TABLE data (
		id        BIGINT      NOT NULL AUTO_INCREMENT,
		date_time DATETIME    NOT NULL,
		src_ip    BIGINT      NOT NULL,
		src_port  INTEGER     NOT NULL,
		dst_ip    BIGINT      NOT NULL,
		dst_port  INTEGER     NOT NULL,
		protocol  VARCHAR(32) NOT NULL,
		CONSTRAINT pk$data PRIMARY KEY (id)
)
		COMMENT = 'Данные по подключениям';
