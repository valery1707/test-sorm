ALTER TABLE conn MODIFY COLUMN orig_bytes BIGINT;
ALTER TABLE conn MODIFY COLUMN resp_bytes BIGINT;
ALTER TABLE conn MODIFY COLUMN orig_ip_bytes BIGINT;
ALTER TABLE conn MODIFY COLUMN resp_ip_bytes BIGINT;
ALTER TABLE conn MODIFY COLUMN missed_bytes BIGINT;

ALTER TABLE http MODIFY COLUMN request_body_len BIGINT;
ALTER TABLE http MODIFY COLUMN response_body_len BIGINT;

ALTER TABLE files MODIFY COLUMN seen_bytes BIGINT;
ALTER TABLE files MODIFY COLUMN total_bytes BIGINT;
ALTER TABLE files MODIFY COLUMN missing_bytes BIGINT;
ALTER TABLE files MODIFY COLUMN overflow_bytes BIGINT;