DROP TRIGGER IF EXISTS bro_binary$host_orig;
CREATE TRIGGER bro_binary$host_orig BEFORE INSERT ON bro_binary FOR EACH ROW SET NEW.id_orig_h_ip = inet_to_decimal(NEW.id_orig_h);

DROP TRIGGER IF EXISTS bro_binary$host_resp;
CREATE TRIGGER bro_binary$host_resp BEFORE INSERT ON bro_binary FOR EACH ROW SET NEW.id_resp_h_ip = inet_to_decimal(NEW.id_resp_h);
