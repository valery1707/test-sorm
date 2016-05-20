ALTER TABLE account_session ADD CONSTRAINT fk$account_session$account_id$account FOREIGN KEY (account_id) REFERENCES account (id);
