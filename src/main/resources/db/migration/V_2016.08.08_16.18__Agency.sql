CREATE TABLE agency (
	id             BIGINT        NOT NULL AUTO_INCREMENT,
	created_by_id  BIGINT        NOT NULL
	COMMENT 'Пользователь, создавший запись',
	created_at     TIMESTAMP     NOT NULL
	COMMENT 'Время создания записи',
	modified_by_id BIGINT        NOT NULL
	COMMENT 'Пользователь, изменивший запись',
	modified_at    TIMESTAMP     NOT NULL
	COMMENT 'Время изменения записи',
	name           VARCHAR(1024) NOT NULL
	COMMENT 'Наименование',
	CONSTRAINT pk$agency PRIMARY KEY (id),
	CONSTRAINT uk$agency$name UNIQUE (name)
)
	COMMENT = 'Наименование органа, осуществляющего проведение ОРМ или надзор';

INSERT INTO agency (created_by_id, created_at, modified_by_id, modified_at, name)
		SELECT
				(SELECT id FROM account WHERE username = 'super' AND is_active), current_timestamp,
				(SELECT id FROM account WHERE username = 'super' AND is_active), current_timestamp,
				src.agency
		FROM (
						 SELECT agency
						 FROM account
						 UNION
						 SELECT agency
						 FROM task
						 UNION
						 SELECT agency
						 FROM task_permit
				 ) src;

ALTER TABLE account ADD COLUMN agency_id BIGINT COMMENT 'Наименование органа, осуществляющего проведение ОРМ или надзор' AFTER agency;
ALTER TABLE task ADD COLUMN agency_id BIGINT COMMENT 'Наименование органа, осуществляющего проведение ОРМ или надзор' AFTER agency;
ALTER TABLE task_permit ADD COLUMN agency_id BIGINT COMMENT 'Наименование органа, осуществляющего проведение ОРМ или надзор' AFTER agency;

UPDATE account SET agency_id = (SELECT id FROM agency WHERE name = agency);
UPDATE task SET agency_id = (SELECT id FROM agency WHERE name = agency);
UPDATE task_permit SET agency_id = (SELECT id FROM agency WHERE name = agency);

ALTER TABLE account DROP COLUMN agency;
ALTER TABLE task DROP COLUMN agency;
ALTER TABLE task_permit DROP COLUMN agency;

UPDATE account SET agency_id = NULL WHERE username IN ('super', 'admin');
