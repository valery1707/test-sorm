ALTER TABLE bro_http ADD COLUMN orig_filenames LONGTEXT COMMENT 'Имя файла из заголовка `Content-Disposition`' AFTER orig_fuids;
UPDATE bro_http SET orig_filenames = filename;
ALTER TABLE bro_http DROP COLUMN filename;
