CREATE TABLE data (
		id        IDENTITY    NOT NULL CONSTRAINT pk$data PRIMARY KEY,
		date_time TIMESTAMP   NOT NULL,
		src_ip    INT8        NOT NULL,
		src_port  INT4        NOT NULL,
		dst_ip    INT8        NOT NULL,
		dst_port  INT4        NOT NULL,
		protocol  VARCHAR(32) NOT NULL
);
COMMENT ON TABLE data IS 'Данные по подключениям';
