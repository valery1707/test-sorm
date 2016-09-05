ALTER TABLE server ADD COLUMN name VARCHAR(256)
AFTER modified_at;

UPDATE server
SET name = 'Караганда'
WHERE host = '192.168.93.130';
