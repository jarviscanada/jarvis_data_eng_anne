#!/bin/bash

# A script to populate host_info table.
# Format: psql_host psql_port db_name psql_user psql_password

# Setting CLI arguments to variables
psql_host=$1
psql_port=$2
database=$3
psql_user=$4
psql_password=$5

hostname=$(hostname -f)

# Linux server information.
lscpu_output=`lscpu`
meminfo=`cat /proc/meminfo`
vmstat=`vmstat -a -S M`
disk=`df -BM /`

timestamp=$(vmstat -t | awk '{if(NR==3) print $18" "$19}' | xargs)

# CPU utilization data.
memory_free=$(echo "$vmstat" | awk '{if(NR==3) print $4}' | xargs)
cpu_idle=$(echo "$disk" | awk '{if(NR==2) print 100-$5}' | xargs)
cpu_kernel=(echo "$meminfo" | egrep "^KernelStack:" | awk '{print $2" "$3}' | xargs)
disk_io=$(vmstat --unit M | awk '{if(NR==3) print $9}' | xargs)
disk_available=$(echo "$disk" | awk '{if(NR==3) print $4}' | xargs)

psql -h $hostname -U $psql_user -w $database_name -p $psql_port -c \
        "INSERT INTO host_usage
         (hostname, timestamp, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available) 
         VALUES ('"$hostname"', $memory_free, $cpu_idle, $disk_io, $disk_available);"

exit 0

