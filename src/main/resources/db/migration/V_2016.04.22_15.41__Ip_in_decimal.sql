ALTER TABLE conn ADD COLUMN id_orig_h_ip DECIMAL(45) COMMENT 'Источник: IP[4-6] в числовом представлении' AFTER id_orig_h;
ALTER TABLE conn ADD COLUMN id_resp_h_ip DECIMAL(45) COMMENT 'Получатель: IP[4-6] в числовом представлении' AFTER id_resp_h;

ALTER TABLE http ADD COLUMN id_orig_h_ip DECIMAL(45) COMMENT 'Источник: IP[4-6] в числовом представлении' AFTER id_orig_h;
ALTER TABLE http ADD COLUMN id_resp_h_ip DECIMAL(45) COMMENT 'Получатель: IP[4-6] в числовом представлении' AFTER id_resp_h;

ALTER TABLE data MODIFY COLUMN src_ip DECIMAL(45);
ALTER TABLE data MODIFY COLUMN dst_ip DECIMAL(45);
