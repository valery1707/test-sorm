##! Скрипт для каждого задания (имя файла включает номер задания)
##! Содержит только необходимые обработки, генерируется автоматически на основе задания

module AMT_TASK_00;

const taskId: count = 1;

event bro_init() &priority=-10 {
	#todo Фильтровать нужно не весь трафик, а трафик только определённого пользователя системы
	AMT::watch_email("gurpartap@patriots.in", taskId);
	AMT::watch_ip_addr(127.0.0.1, taskId);
	AMT::watch_ip_addr(178.89.247.179, taskId);
	AMT::watch_ip_addr(178.89.247.180, taskId);
}
