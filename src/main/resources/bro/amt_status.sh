#!/usr/bin/env bash

log_dir=/opt/bro/logs/current
sql_dir=/home/amtadmin/bro_tmp
names="conn http files smtp"
db_username=amt
db_password=strong_password
db_database=amt_karaganda

table="  SOURCE LOG_FILE SQL_FILE DATABASE DIFF MULT"
for n in ${names}; do
	cnt_log=$(cat ${log_dir}/${n}.log | /opt/bro/bin/bro-cut uid | wc -l)
	cnt_sql=$(wc -l ${sql_dir}/bro_${n}.sql.log | cut -d' ' -f1)
	cnt_db=$(echo "SELECT count(*) FROM bro_${n};" | mysql --skip-column-names --user=${db_username} ${db_database})
	diff_raw=$(echo "${cnt_log} - ${cnt_db}" | bc)
	if [ "0" == ${cnt_db} ]; then
		diff_mult='(inf)'
	else
		diff_mult=$(echo "scale=2; ${cnt_log} / ${cnt_db}" | bc)
	fi
	table+="(NL)${n} ${cnt_log} ${cnt_sql} ${cnt_db} ${diff_raw} ${diff_mult}"
done
echo ${table} | sed 's/(NL)/\n/g' | column -t
sql --db-size mysql://${db_username}@localhost/${db_database}
