DROP DATABASE IF EXISTS host_agent;
CREATE DATABASE host_agent;

\c host_agent

DROP TABLE IF EXISTS PUBLIC.host_info;
DROP TABLE IF EXISTS PUBLIC.host_usage;

CREATE TABLE PUBLIC.host_info
    (
        id			SERIAL NOT NULL UNIQUE,
        hostname		VARCHAR NOT NULL UNIQUE,
	cpu_number		INT2 NOT NULL,
	cpu_architecture	VARCHAR NOT NULL,
	cpu_model		VARCHAR NOT NULL,
	cpu_mhz			FLOAT8 NOT NULL,
	l2_cache		INT4 NOT NULL,
	total_mem		INT4 NOT NULL,
	timestamp		TIMESTAMP NOT NULL,
	PRIMARY KEY (id, hostname)
    );

CREATE TABLE PUBLIC.host_usage
    (
	id		SERIAL NOT NULL,
	hostname 	VARCHAR NOT NULL REFERENCES host_info(hostname),
	memory_free	INT4 NOT NULL,
	cpu_idle	INT2 NOT NULL,
	cpu_kernel	INT2 NOT NULL,
	disk_io		INT2 NOT NULL,
	disk_available	INT4 NOT NULL,
	timestamp	TIMESTAMP NOT NULL,
	PRIMARY KEY (id, timestamp),
	FOREIGN KEY (id) REFERENCES host_info(id)
    );
