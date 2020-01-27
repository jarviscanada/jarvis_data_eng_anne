/* Sample queries to help the user better track/manage cluster and plan for
/* future resources.

/* Query 1
/* Group hosts by CPU number and sort by their memory size in descending order

SELECT
	cpu_number,
	id AS host_id,
	total_mem
FROM
	host_info
ORDER BY
	cpu_number,
	total_mem desc;

/* Query 2
/* Average used memory in percentage over 5 mins interval for each host.

SELECT window(host_id, host_name, total_memory, used_memory_percentage) OVER w
FROM host_usage
select date_trunc('minute', now())

SELECT 
	host_info.id AS host_id,
	host_info.hostname AS host_name,
	rounded_usage.rounded_timestamp AS timestamp,
	round(
		avg(
			host_info.total_mem/1042 - rounded_usage.memory_free
			) / (host_info.total_mem / 1024) * 100), 
	2
	) as avg_used_memory_percentage
FROM 
	host_usage AS usage
	JOIN
		host_info AS info
		ON usage.host_id = info.id
GROUP BY
	usage.host_id
	info.hostname,
	DATE_TRUNC('hour', usage."timestamp") + interval '5 MINUTE' * (DATE_PART('MINUTE',usage."timestamp")/5.0)::int
ORDER BY
	usage.host_id;
