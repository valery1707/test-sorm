##! Скрипт для каждого задания (имя файла включает номер задания)
##! Содержит только необходимые обработки, генерируется автоматически на основе задания
## Client alias: {{clientAlias}}
## Note: {{note}}
## Agency: {{agency}}

module AMT_TASK_{{id}};

const taskId: count = {{id}};

event bro_init() &priority=-10 {
	#todo Фильтровать нужно не весь трафик, а трафик только определённого пользователя системы
	{{#filter.email}}
		{{#this.value}}
	AMT::watch_email("{{this.value}}", taskId);
		{{/this.value}}
	{{/filter.email}}
	AMT::watch_ip_addr(127.0.0.1, taskId);
	AMT::watch_ip_addr(178.89.247.179, taskId);
	AMT::watch_ip_addr(178.89.247.180, taskId);
}
