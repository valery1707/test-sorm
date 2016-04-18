ALTER TABLE conn
CHANGE COLUMN `id.orig_h` id_orig_h VARCHAR(45),
CHANGE COLUMN `id.orig_p` id_orig_p INT,
CHANGE COLUMN `id.resp_h` id_resp_h VARCHAR(45),
CHANGE COLUMN `id.resp_p` id_resp_p INT;
