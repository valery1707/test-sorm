##! Скрипт с глобальными настройками: 
##! * Добавление полей в логи
##! * Создание фильтров для записи данных в БД
##! * Обработка вложенных файлов

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

module AMT;

# Добавление ссылки на тикет в conn и остальные используемые типы логов где это нужно
redef record Conn::Info += {
	amt_tasks: set[count] &default=set();
	amt_tasks_list: string &default="" &log;
};

export {
	global catch_conn: function(c: connection, taskId: count): bool;
}

# Хранение списка подходящих коннектов - для отслеживания вложенных сущностей
global catched_conn_uid: table[string] of set[count] = {};
global catched_conn_id: set[conn_id] = {};

# Функции для проверки на то что коннект нам нужен
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


# Функции для фильтрации логов
function filter_conn(rec: Conn::Info): bool {
	if (rec$uid in catched_conn_uid) {
		local tasks_list = ",";
		for (taskId in catched_conn_uid[rec$uid]) {
			tasks_list = cat(tasks_list, ",", taskId);
		}
		rec$amt_tasks_list = tasks_list;
		return T;
	}
	return F;
}
function filter_smtp(rec: SMTP::Info): bool {
	return is_catched_conn_single(rec$uid);
}


event bro_init(){
	print "AMT::start";
	local filter = Log::get_filter(Conn::LOG, "default");
	filter$pred = filter_conn;
	Log::add_filter(Conn::LOG, filter);
	filter = Log::get_filter(SMTP::LOG, "default");
	filter$pred = filter_smtp;
	Log::add_filter(SMTP::LOG, filter);
}

event bro_done() {
	print "AMT::stop";
}

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
