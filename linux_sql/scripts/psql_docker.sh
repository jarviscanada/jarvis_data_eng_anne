#!/bin/bash
# PSQL_Docker.sh script
# The purpose of this script is to start/stop Docker and start/stop a PostgreSQL container.

# Setting up arguments
given_command=$1
password=$2

# Setting up container details
container_name=jrvis-psql
container_ports=5432:5432
volume_name=pgdata
volume_info=${volume_name}:/var/lib/postgresql/data

# Checking arguments, starting with the case of invalid number of arguments.
if ["$#" > "2"];
then
    echo "Invalid number of arguments. Please run the script with either the command start followed by the database password, or stop."
    exit 1
fi

# If the command is neither 'start' or 'stop.'
if [ $given_command != "start" ] && [ $given_command != "stop" ];
then
    echo "Invalid command. Please run the script with either the command start followed by the database password, or stop."
    exit 1
fi

# If the command 'stop' is given, but they include a second parameter:
if [ $given_command = "stop" && "$#" = "2"];
then
	echo "Too many parameters provided. Please use 'stop' command with no additional arguments."
fi

# If the command 'start' is given but they forgot the password:
if [ $given_command = "start" ] && [ "$#" != "2" ];
then
    echo "Incorrect number of commmand(s)."
    if [ "$#" = "1" ];
    then
        echo "You forgot your password!"
    fi
    exit 1
fi

# Start addressing valid command arguments.
# If the command 'stop' is given, stop the running container.
if [ $given_command = "stop" ];
then
	container_id=$(docker container ls -q -f name=$container_name)
	if [ -z "${container_id}"];
	then
		echo "Container is not running."
		exit 1
	fi

else
    docker container stop $container_name
    echo "Container stopped."
    exit 0
fi


# Now starting the script where the command is "start [password]"

# Checking to see if docker is running.
systemctl status docker || systemctl start docker
echo "Docker running."

# Checks to see if the container is running.
if [ "$(docker ps -f name=$container_name | wc -l)" = "2" ];
then
    echo "Docker container is running!"
    exit 0
fi

# If it isn't running, starts setting up.
# Check to see of the docker volume is created.
if [ "$(docker volume ls | wc -l)" != "2" ];
then
    docker volume create $volume_name
    echo "Docker volume initialized!"
else
    echo "Docker volume already exists!"
fi

# Checks if the container exists. If it doesn't, create it. If it does,
# then run the existing container image.
PGPASSWORD=$2

if [ "$(docker container ls -a -f name=$container_name | wc -l)" != "2" ];
then
    docker run --name $container_name -e POSTGRES_PASSWORD=$PGPASSWORD -d -v $volume_info -p $container_ports postgres
else
    docker container start $container_name
echo "Docker container started!"
fi

exit 0