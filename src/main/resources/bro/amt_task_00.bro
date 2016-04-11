##! Скрипт для каждого задания (имя файла включает номер задания)
##! Содержит только необходимые обработки, генерируется автоматически на основе задания

module AMT_TASK_00;

const taskId: count = 0;

event bro_init() &priority=-10 {
	AMT::watch_email("gurpartap@patriots.in", taskId);
}
