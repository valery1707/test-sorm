ALTER TABLE bro_conn ADD INDEX idx$bro_conn$created_at (created_at);
ALTER TABLE bro_conn ADD INDEX idx$bro_conn$created_by (created_by_id);

ALTER TABLE bro_files ADD INDEX idx$bro_files$created_at (created_at);
ALTER TABLE bro_files ADD INDEX idx$bro_files$created_by (created_by_id);

ALTER TABLE bro_http ADD INDEX idx$bro_http$created_at (created_at);
ALTER TABLE bro_http ADD INDEX idx$bro_http$created_by (created_by_id);

ALTER TABLE bro_smtp ADD INDEX idx$bro_smtp$created_at (created_at);
ALTER TABLE bro_smtp ADD INDEX idx$bro_smtp$created_by (created_by_id);
