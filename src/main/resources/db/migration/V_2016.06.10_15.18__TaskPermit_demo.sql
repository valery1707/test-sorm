INSERT INTO task_permit (created_by_id, created_at, agency, task_id, account_id, period_start, period_finish)
VALUES
		((SELECT id FROM account WHERE username = 'admin'), now(), (SELECT agency FROM account WHERE username = 'admin'), 1,
		 (SELECT id FROM account WHERE username = 'operator'), '2016-06-01 00:00:00', '2016-12-31 23:59:59');
