#!/bin/bash

# A script to populate host_info table.
# Format: psql_host psql_port db_name psql_user psql_password

# Setting CLI arguments to variables
psql_host=$1
psql_port=$2
database=$3
psql_user=$4
psql_password=$5

# Saves host name to a variable.
hostname=$(hostname -f)

# Linux server information.
lscpu_output=`lscpu`
meminfo=`cat /proc/meminfo`

# Save the CPU hardware into relevant field variables.
cpu_number=$(echo "$lscpu_output" | egrep "^CPU\(s\):" | awk '{print $2}' | xargs)
cpu_architecture=$(echo "$lscpu_output" | egrep "^Architecture:" | awk '{print $2}' | xargs)
cpu_model=$(echo "$lscpu_output" | egrep "^Model name:" | awk -F':' '{print $2}' | xargs)
cpu_mhz=$(echo "$lscpu_output" | egrep "^CPU MHz:" | awk -F':' '{print $2}' | xargs)
l2_cache=$(echo "$lscpu_output" | egrep "^L2 cache:" | awk '{print $3}' | xargs)
total_mem=$(echo "$meminfo" | egrep "MemTotal:" | awk '{print $2}' | xargs)
timestamp=$(vmstat -t | awk '{if(NR==3) print $18" "$19}' | xargs)

psql -h $hostname -U $psql_user -w $database_name -p $psql_port -c \
	"INSERT INTO host_info
	 (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, total_mem, timestamp) 
	 VALUES ('"$hostname"', '"$cpu_number"', '"$cpu_architecture"', '"$cpu_model"', '"$cpu_mhz"', '"$l2_cache"', '"$total_mem"', '"$timestamp"');"

exit 0
