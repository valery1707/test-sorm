#!/bin/bash

_info=1
_debug=0
db_warm_up_cnt=3

_help() {
	echo "Watch Bro log files and pipe lines into MySQL
Usage: $0 (mysql_username) (mysql_password) (mysql_database) [log_dir] [log_file_prefix]"
}
_info() {
	if [ ${_info} -ge 1 ]; then
		echo "INFO: $1"
	fi
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
log_dir=${4:-/opt/bro/logs/current}
log_file_prefix=$5

_debug "Params: DbUser(${db_username}), DbPass(${db_password}), DbName(${db_database}), LogDir(${log_dir}), LogFilePrefix(${log_file_prefix})"

_info "Warm up MySQL"
echo "[client]
password=${db_password}" > ~/.my.cnf
chmod 600 ~/.my.cnf
OUT=1
while [ ${OUT} -ne 0 ] && [ ${db_warm_up_cnt} -gt 0 ]; do
	let db_warm_up_cnt=db_warm_up_cnt-1
	RES=$(echo "SELECT NOW();" | mysql --user=${db_username} ${db_database} 2>&1)
	OUT=$?
	_debug "MySQL warm up: ${RES}"
	if [ ${OUT} -ne 0 ]; then
		sleep 1s
	fi
done
if [ ${db_warm_up_cnt} -le 0 ]; then
	echo "ERROR: MySQL is not accessible: ${RES}"
	exit 2
fi

_info "Stop old log watchers"
killall -v -9 tail

#$1 - file_name
#$2 - table_name
#$3 - table_columns
_watch() {
	file_name=$1
	table_name=$2
	table_columns=$(echo ${3} | sed 's/\./_/g')
	bro_columns=$(echo ${3} | sed 's/,//g' | sed 's/`//g')
	file_name_full="${log_dir}/${log_file_prefix}${file_name}"
	tail --retry --lines=+0 --follow ${file_name_full} \
| /opt/bro/bin/bro-cut ${bro_columns} \
| sed 's/"/_saved_double_quote_/g' \
| sed "s/'/_saved_single_quote_/g" \
| sed -r 's_\t_", "_g' \
| awk '{print "\"" $0 "\""}' \
| sed 's_"-"_NULL_g' \
| sed 's_"(empty)"_NULL_g' \
| sed 's_"F"_FALSE_g' | sed 's_"T"_TRUE_g' \
| sed -r 's_"(([[:digit:]]+)(\.[[:digit:]]+)?)"_\1_g' \
| sed "s_\"_'_g" \
| awk '{print "INSERT INTO target_table_name(target_table_columns) VALUES (" $0 ");"}' \
| sed "s/target_table_name/${table_name}/g" \
| sed "s/target_table_columns/${table_columns}/g" \
| sed 's/_saved_double_quote/"_/g' \
| sed "s/_saved_single_quote_/''/g" \
| tee ${table_name}.sql.log \
| mysql --batch --force --reconnect --wait --unbuffered --user=${db_username} ${db_database} \
&
}

_info "Run new log watchers"
_watch conn.log bro_conn 'ts, uid, id.orig_h, id.orig_p, id.resp_h, id.resp_p, proto, service, duration, orig_bytes, resp_bytes, conn_state, local_orig, local_resp, missed_bytes, history, orig_pkts, orig_ip_bytes, resp_pkts, resp_ip_bytes, tunnel_parents, amt_tasks_list'
_watch http.log bro_http 'ts, uid, id.orig_h, id.orig_p, id.resp_h, id.resp_p, trans_depth, method, host, uri, referrer, user_agent, request_body_len, response_body_len, status_code, status_msg, info_code, info_msg, filename, tags, username, password, proxied, orig_fuids, orig_mime_types, resp_fuids, resp_mime_types'
_watch files.log bro_files 'ts, fuid, tx_hosts, rx_hosts, conn_uids, source, depth, analyzers, mime_type, filename, duration, local_orig, is_orig, seen_bytes, total_bytes, missing_bytes, overflow_bytes, timedout, parent_fuid, md5, sha1, sha256, extracted'
_watch smtp.log bro_smtp 'ts, uid, id.orig_h, id.orig_p, id.resp_h, id.resp_p, trans_depth, helo, mailfrom, rcptto, date, `from`, `to`, reply_to, msg_id, in_reply_to, subject, x_originating_ip, first_received, second_received, last_reply, path, user_agent, tls, fuids, is_webmail'

#ps -ef|grep 'tail'
