DROP FUNCTION IF EXISTS inet_to_decimal;

DELIMITER //

CREATE FUNCTION inet_to_decimal(str VARCHAR(60))
		RETURNS DECIMAL(45)
LANGUAGE SQL
DETERMINISTIC
NO SQL
		BEGIN
				DECLARE str_hex VARCHAR(45);
				IF is_ipv6(str)
				THEN
						SET str_hex = HEX(inet6_aton(str));
						RETURN cast(conv(substr(str_hex, 1, 16), 16, 10) AS DECIMAL(45)) * 18446744073709551616 + cast(conv(substr(str_hex, 17, 16), 16, 10) AS DECIMAL(45));
				ELSE
						RETURN cast(inet_aton(str) AS DECIMAL(45));
				END IF;
		END//

DELIMITER ;

/*
--Demo
SELECT inet_to_decimal('178.89.247.180'), inet_to_decimal('fe80::20c:29ff:feaa:296f');
SELECT hex(inet_to_decimal('178.89.247.180')), inet_to_decimal('fe80::20c:29ff:feaa:296f');
*/

UPDATE conn SET id_orig_h_ip = inet_to_decimal(id_orig_h) WHERE id_orig_h_ip IS NULL;
UPDATE conn SET id_resp_h_ip = inet_to_decimal(id_resp_h) WHERE id_resp_h_ip IS NULL;
UPDATE http SET id_orig_h_ip = inet_to_decimal(id_orig_h) WHERE id_orig_h_ip IS NULL;
UPDATE http SET id_resp_h_ip = inet_to_decimal(id_resp_h) WHERE id_resp_h_ip IS NULL;

DROP TRIGGER IF EXISTS conn_inet_orig;
CREATE TRIGGER conn_inet_orig BEFORE INSERT ON conn FOR EACH ROW SET NEW.id_orig_h_ip = inet_to_decimal(NEW.id_orig_h);
DROP TRIGGER IF EXISTS conn_inet_resp;
CREATE TRIGGER conn_inet_resp BEFORE INSERT ON conn FOR EACH ROW SET NEW.id_resp_h_ip = inet_to_decimal(NEW.id_resp_h);

DROP TRIGGER IF EXISTS http_inet_orig;
CREATE TRIGGER http_inet_orig BEFORE INSERT ON http FOR EACH ROW SET NEW.id_orig_h_ip = inet_to_decimal(NEW.id_orig_h);
DROP TRIGGER IF EXISTS http_inet_resp;
CREATE TRIGGER http_inet_resp BEFORE INSERT ON http FOR EACH ROW SET NEW.id_resp_h_ip = inet_to_decimal(NEW.id_resp_h);
