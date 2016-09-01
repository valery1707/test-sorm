ALTER TABLE server ADD COLUMN name TEXT
AFTER modified_at;

UPDATE server
SET name = 'Караганда'
WHERE host = '192.168.93.130';
