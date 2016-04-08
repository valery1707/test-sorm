##! Скрипт для каждого задания (имя файла включает номер задания)
##! Содержит только необходимые обработки, генерируется автоматически на основе задания

module AMT_TASK_00;

const taskId: count = 0;

# Обработка POP3/SMTP
event mime_one_header(c: connection, h: mime_header_rec) {
#	print fmt("mime_one_header(c: {uid: '%s'}, h: {name: '%s', value: '%s'})", c$uid, h$name, h$value);
	if (h$name == "FROM" && /gurpartap@patriots\.in/ in h$value) {
		AMT::catch_conn(c, taskId);
	}
}
