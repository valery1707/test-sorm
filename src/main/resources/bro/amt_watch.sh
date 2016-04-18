#!/bin/bash

_debug=0

_help() {
	echo "Watch Bro log files and pipe lines into MySQL
Usage: $0 (mysql_username) (mysql_password) (mysql_database) [log_dir] [log_file_prefix]"
}
_debug() {
	if [ ${_debug} -ge 1 ]; then
		echo "DEBUG: $1"
	fi
}

if [ "x" == "x$3" ]; then
	_help
	exit 1
fi
db_username=$1
db_password=$2
db_database=$3
log_dir=${4:-/opt/bro/log/current}
log_file_prefix=$5

_debug "Params: DbUser(${db_username}), DbPass(${db_password}), DbName(${db_database}), LogDir(${log_dir}), LogFilePrefix(${log_file_prefix})"

killall -v -9 tail

#$1 - file_name
#$2 - table_name
#$3 - table_columns
_watch() {
	file_name=$1
	table_name=$2
	table_columns=$3
	bro_columns=$(echo ${table_columns} | sed 's_`__g' | sed 's_,__g')
	file_name_full="${log_dir}/${log_file_prefix}${file_name}"
	touch ${file_name_full}
	tail -f -n+0 ${file_name_full} \
| /opt/bro/bin/bro-cut ${bro_columns} \
| sed -r 's_\t_", "_g' \
| awk '{print "\"" $0 "\""}' \
| sed 's_"-"_NULL_g' \
| sed 's_"(empty)"_NULL_g' \
| sed 's_"F"_FALSE_g' | sed 's_"T"_TRUE_g' \
| sed -r 's_"(([[:digit:]]+)(\.[[:digit:]]+)?)"_\1_g' \
| sed "s_\"_'_g" \
| awk '{print "INSERT INTO target_table_name(target_table_columns) \nVALUES (" $0 ");"}' \
| sed "s/target_table_name/${table_name}/g" \
| sed "s/target_table_columns/${table_columns}/g" \
| mysql --user=${db_username} --password=${db_password} ${db_database} --batch &
}

_watch conn.log conn 'ts, uid, `id.orig_h`, `id.orig_p`, `id.resp_h`, `id.resp_p`, proto, service, duration, orig_bytes, resp_bytes, conn_state, local_orig, local_resp, missed_bytes, history, orig_pkts, orig_ip_bytes, resp_pkts, resp_ip_bytes, tunnel_parents, amt_tasks_list'

#ps -ef|grep 'tail'
