ALTER TABLE task COMMENT = 'Задание на перехват данных';
ALTER TABLE task MODIFY COLUMN agency VARCHAR(1024) NOT NULL COMMENT 'Наименование органа, осуществляющего проведение ОРМ или надзор';
ALTER TABLE task MODIFY COLUMN client_alias VARCHAR(1024) NOT NULL COMMENT 'Псевдоним абонента';
ALTER TABLE task MODIFY COLUMN note VARCHAR(1024) COMMENT 'Примечание';
