-- Password same as username
INSERT INTO account (username, password, is_active, role, active_until, agency)
VALUES
		('super', '$2a$10$TofPFKn1jrdu1u4a6NA1YOodnl5KHjPHdo88z/EoZmFcVdI5MwrB2', TRUE, 'SUPER', NULL, 'AstanaMegaTel'),
		('operator', '$2a$10$.0dzhXrpBigz7Zmb5mM9qOFGDfyiXG4HeKjQ44cBzPugtL/iDzWmu', TRUE, 'OPERATOR', NULL, 'AstanaMegaTel'),
		('supervisor', '$2a$10$5HkVdI3QJIqORyus4mBH.OiD7PrAYgzQ1Y//iXPtYVXE.8E4i03C.', TRUE, 'SUPERVISOR', NULL, 'AstanaMegaTel');

UPDATE account
SET password = '$2a$10$iW/2Uq/HW.3WavCEU5QBqu1dEvmyzrJgV15.0q9bsKLa/2ZI1bq96',
		role     = 'ADMIN'
WHERE username = 'admin';
