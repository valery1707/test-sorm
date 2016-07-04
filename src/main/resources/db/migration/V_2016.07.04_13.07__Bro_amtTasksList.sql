ALTER TABLE bro_smtp ADD COLUMN amt_tasks_list VARCHAR(1024) COMMENT 'Привязка к задачам внутри системы';
UPDATE bro_smtp SET amt_tasks_list = ',1,';

ALTER TABLE bro_http ADD COLUMN amt_tasks_list VARCHAR(1024) COMMENT 'Привязка к задачам внутри системы';
UPDATE bro_http SET amt_tasks_list = ',1,';
