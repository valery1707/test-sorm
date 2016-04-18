ALTER TABLE conn ADD COLUMN id_orig_h VARCHAR(45) AFTER `id.orig_h`;
ALTER TABLE conn ADD COLUMN id_orig_p INT AFTER `id.orig_p`;
ALTER TABLE conn ADD COLUMN id_resp_h VARCHAR(45) AFTER `id.resp_h`;
ALTER TABLE conn ADD COLUMN id_resp_p INT AFTER `id.resp_p`;

UPDATE conn
SET
		id_orig_h = `id.orig_h`,
		id_orig_p = `id.orig_p`,
		id_resp_h = `id.resp_h`,
		id_resp_p = `id.resp_p`;

ALTER TABLE conn DROP COLUMN `id.orig_h`;
ALTER TABLE conn DROP COLUMN `id.orig_p`;
ALTER TABLE conn DROP COLUMN `id.resp_h`;
ALTER TABLE conn DROP COLUMN `id.resp_p`;
