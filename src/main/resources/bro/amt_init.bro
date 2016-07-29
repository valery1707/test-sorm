##! Скрипт с глобальными настройками:
##! * Добавление полей в логи
##! * Создание фильтров для записи данных в БД
##! * Обработка вложенных файлов

##![Types](https://www.bro.org/sphinx/script-reference/types.html)
##![Declarations and Statements](https://www.bro.org/sphinx/script-reference/statements.html)
##![Functions](https://www.bro.org/sphinx/scripts/base/bif/bro.bif.bro.html)

@load base/protocols/conn/main.bro

#https://www.bro.org/sphinx/scripts/base/init-bare.bro.html#type-fa_file
@load base/frameworks/files/main.bro
@load base/protocols/ftp/files.bro
@load base/protocols/http/entities.bro

#https://www.bro.org/sphinx/scripts/base/frameworks/files/main.bro.html#type-Files::Info
@load base/files/hash/main.bro
@load base/files/extract/main.bro

#https://www.bro.org/sphinx/frameworks/logging-input-sqlite.html#id6
#@load frameworks/files/hash-all-files

#https://www.bro.org/sphinx/scripts/base/protocols/smtp/main.bro.html#type-SMTP::Info
@load policy/protocols/smtp/software.bro

##!Обработка бинарных протоколов
@load base/protocols/crypted/main.bro
@load-sigs ./amt_binary.sig

module AMT;

# Добавление ссылки на тикет в conn и остальные используемые типы логов где это нужно
#--------------------------------------------------#
#--------------------------------------------------#
#--------------------------------------------------#
redef record Conn::Info += {
	amt_tasks: set[count] &default=set();
	amt_tasks_list: string &default="" &log;
};
redef record SMTP::Info += {
	amt_tasks: set[count] &default=set();
	amt_tasks_list: string &default="" &log;
};
redef record HTTP::Info += {
	amt_tasks: set[count] &default=set();
	amt_tasks_list: string &default="" &log;
};

export {
	## Сохранение данных о коннекте
	##
	## c: Коннект
	## taskId: Номер задания
	global catch_conn: function(c: connection, taskId: count): bool;

	## Слежение за почтой
	##
	## s: eMail
	## taskId: Номер задания
	global watch_email: function(s: string, taskId: count);

	## Слежение за трафиком по IP
	##
	## target: IPv4/IPv6
	## taskId: Номер задания
	global watch_ip_addr: function(target: addr, taskId: count);
}

#--------------------------------------------------#
# Хранение списка подходящих коннектов - для отслеживания вложенных сущностей
#--------------------------------------------------#
## table[conn$uid] => set[taskId]
global catched_conn_uid: table[string] of set[count] = {};
#todo Remove?
global catched_conn_id: set[conn_id] = {};

#--------------------------------------------------#
# Функции для проверки на то что коннект нам нужен
#--------------------------------------------------#
function is_catched_conn_single(conn_uid: string): bool {
	return conn_uid in catched_conn_uid;
}
function is_catched_conn_set(conn_uids: set[string]): bool {
	for (uid in conn_uids) {
		if (uid in catched_conn_uid) {
			return T;
		}
	}
	return F;
}
function is_catched_conn_table(conn: table[conn_id] of connection): bool {
	for (id in conn) {
		if (is_catched_conn_single(conn[id]$uid)) {
			return T;
		}
	}
	return F;
}


#--------------------------------------------------#
# Функции для фильтрации логов
#--------------------------------------------------#
function cat_set(src: set[count], prefix: string, delimiter: string, suffix: string): string {
	local res = prefix;
	local cnt: count = |src|;
	local first = T;
	for (item in src) {
		if (first) {
			res = cat(res, item);
			first = F;
		} else {
			res = cat(res, delimiter, item);
		}
	}
	if (cnt > 0) {
		res = res + suffix;
	}
	return res;
}

function filter_conn(rec: Conn::Info): bool {
	if (rec$uid in catched_conn_uid) {
		rec$amt_tasks_list = cat_set(catched_conn_uid[rec$uid], ",", ",", ",");
		return T;
	}
	return F;
}
function filter_smtp(rec: SMTP::Info): bool {
	if (rec$uid in catched_conn_uid) {
		rec$amt_tasks_list = cat_set(catched_conn_uid[rec$uid], ",", ",", ",");
		return T;
	}
	return F;
}
function filter_http(rec: HTTP::Info): bool {
	if (rec$uid in catched_conn_uid) {
		rec$amt_tasks_list = cat_set(catched_conn_uid[rec$uid], ",", ",", ",");
		return T;
	}
	return F;
}
function filter_files(rec: Files::Info): bool {
	return is_catched_conn_set(rec$conn_uids);
}


#--------------------------------------------------#
# Инициализация скриптов
#--------------------------------------------------#
event bro_init() {
	print "AMT::start";

	# Conn
	local filter = Log::get_filter(Conn::LOG, "default");
	filter$pred = filter_conn;
	Log::add_filter(Conn::LOG, filter);

	# SMTP
	filter = Log::get_filter(SMTP::LOG, "default");
	filter$pred = filter_smtp;
	Log::add_filter(SMTP::LOG, filter);

	# HTTP
	filter = Log::get_filter(HTTP::LOG, "default");
	filter$pred = filter_http;
	Log::add_filter(HTTP::LOG, filter);

	# Files
	filter = Log::get_filter(Files::LOG, "default");
	filter$pred = filter_files;
	Log::add_filter(Files::LOG, filter);
}
event bro_done() {
	print "AMT::stop";
}


#--------------------------------------------------#
# Глобальные функции
#--------------------------------------------------#
function catch_conn(c: connection, taskId: count): bool {
	local tasks: set[count];
	if (c$uid in catched_conn_uid) {
		tasks = catched_conn_uid[c$uid];
	} else {
		tasks = set();
	}
	if (taskId in tasks) {
		# Already catched
		return F;
	}
	add tasks[taskId];
	catched_conn_uid[c$uid] = tasks;
	return T;
}
function catch_conn_multi(c: connection, taskIds: set[count]): bool {
	local tasks: set[count];
	if (c$uid in catched_conn_uid) {
		tasks = catched_conn_uid[c$uid];
	} else {
		tasks = set();
	}
	local catchedNow = F;
	for (task in taskIds) {
		if (! (task in tasks)) {
			catchedNow = T;
			add tasks[task];
		}
	}
	if (catchedNow) {
		catched_conn_uid[c$uid] = tasks;
	}
	return catchedNow;
}

## table[email] => set[taskId]
global watch_emails: table[string] of set[count] = {};
## table[email] => pattern
global watch_emails_p: table[string] of pattern = {};
function watch_email(s: string, taskId: count) {
	#todo URL
	local p: pattern = string_to_pattern(s, T);
	local tasks: set[count] = set();
	if (s in watch_emails) {
		tasks = watch_emails[s];
	}
	add tasks[taskId];
	watch_emails[s] = tasks;
	watch_emails_p[s] = p;
}

## table[addr] => set[taskId]
global watch_ip_addrs: table[addr] of set[count] = {};
function watch_ip_addr(target: addr, taskId: count) {
	local tasks: set[count] = set();
	if (target in watch_ip_addrs) {
		tasks = watch_ip_addrs[target];
	}
	add tasks[taskId];
	watch_ip_addrs[target] = tasks;
}


#--------------------------------------------------#
#---------- Отслеживание полезных событий ---------#
#--------------------------------------------------#

# Отслеживание коннектов по IP
event new_connection(c: connection) {
	if (! is_catched_conn_single(c$uid)) {#todo Не верно, так как один и тот же коннект может относится к разным заданиям по разным фильтрам
		if (c$id$orig_h in watch_ip_addrs) {
			catch_conn_multi(c, watch_ip_addrs[c$id$orig_h]);
		} else if (c$id$resp_h in watch_ip_addrs) {
			catch_conn_multi(c, watch_ip_addrs[c$id$resp_h]);
		}
	}
}

# Отслеживание eMail
event mime_one_header(c: connection, h: mime_header_rec) {
#	print fmt("mime_one_header(c: {uid: '%s'}, h: {name: '%s', value: '%s'})", c$uid, h$name, h$value);
	if (! is_catched_conn_single(c$uid)) {#todo Не верно, так как один и тот же коннект может относится к разным заданиям по разным фильтрам
		if (h$name == "FROM" || h$name == "TO") {#todo Нужно отслеживать поля "Копия" и "Скрытая копия"
#			print fmt("Header: %s=%s", h$name, h$value);
			for (p in watch_emails) {
#				print fmt("Pattern: %s", p);
				if (watch_emails_p[p] in h$value) {
					print fmt("Catch Mime: conn$uid=%s, h$name=%s, h$value=%s", c$uid, h$name, h$value);
					catch_conn_multi(c, watch_emails[p]);
				}
			}
		}
	}
}

# Сохранение файлов из отслеживаемых коннектов
event file_new(f: fa_file) {
	if (is_catched_conn_table(f$conns)) {
		Files::add_analyzer(f, Files::ANALYZER_EXTRACT);
	}
}

event file_sniff(f: fa_file, meta: fa_metadata) {
#	local fname = fmt("%s-%s.%s", f$source, f$id, meta$mime_type);
#	print fmt("Extracting file %s", fname);
#	local mime_type = (meta?$mime_type) ? meta$mime_type : "NONE";
#	print fmt("Source: %s; ID: %s; Mime: %s", f$source, f$id, mime_type);
#	if (f?$info) {
#		print fmt("  Info! ts: %s; mime: %s; filename: %s; md5: %s", f$info$ts, f$info?$mime_type ? f$info$mime_type : "NONE", f$info?$filename ? f$info$filename : "???", f$info?$md5 ? f$info$md5 : "000");
#	}
}

# Обработка событий совпадения сигнатур для генерации сообщений CRYPTED для связи коннекта с протоколом
event signature_match(state: signature_state, msg: string, data: string) {
	print fmt("Signature: (sig:%s, msg: %s)", state$sig_id, msg);
	# todo Генерация события CRYPTED если msg начинается с "sig-bin-"
	if (/^sig-bin-/ in msg) {
		local info: Crypted::Info;
		info$ts  = network_time();
		info$uid = state$conn$uid;
		info$id  = state$conn$id;
		info$protocol = str_split(msg, vector(8))[1];

		Log::write(Crypted::LOG, info);
	}
}
