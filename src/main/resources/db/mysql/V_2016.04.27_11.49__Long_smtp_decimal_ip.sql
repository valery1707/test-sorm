DROP TRIGGER IF EXISTS smtp_inet_orig;
CREATE TRIGGER smtp_inet_orig BEFORE INSERT ON smtp FOR EACH ROW SET NEW.id_orig_h_ip = inet_to_decimal(NEW.id_orig_h);

DROP TRIGGER IF EXISTS smtp_inet_resp;
CREATE TRIGGER smtp_inet_resp BEFORE INSERT ON smtp FOR EACH ROW SET NEW.id_resp_h_ip = inet_to_decimal(NEW.id_resp_h);

DROP TRIGGER IF EXISTS smtp_inet_resp;
CREATE TRIGGER smtp_inet_resp BEFORE INSERT ON smtp FOR EACH ROW SET NEW.x_originating_ip_ip = inet_to_decimal(NEW.x_originating_ip);
