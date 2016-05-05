INSERT INTO task (id, created_at, agency, client_alias, period_start, period_finish, note)
VALUES (1, now(), 'MegaTel', 'Test', '2016-04-01 00:00:00', '2016-05-31 23:59:59', 'Note');

UPDATE bro_conn
SET amt_tasks_list = replace(amt_tasks_list, ',0,', ',1,')
WHERE amt_tasks_list LIKE '%,0,%';
