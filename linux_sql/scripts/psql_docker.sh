#!/bin/bash
# PSQL_Docker.sh script
# The purpose of this script is to start/stop Docker and start/stop a PostgreSQL container. 

# Checking arguments, starting with the case of invalid arguments.
if [ "$1" != "start" ] && [ "$1" != "stop" ];
then
    echo "Invalid command. Please run the script with either the command start followed by the database password, or stop."
    exit 1
fi

# If the command 'stop' is given, stop the running container.
if [ "$1" = "stop" ];
then
   docker container stop jrvs-psql
    echo "Container stopped."
    exit 0
fi

# If the command 'start' is given but they forgot the password:
if [ "$1" = "start" ] && [ "$#" != "2" ];
then
    echo "Incorrect number of commmand(s)."
    if [ "$#" = "1" ];
    then
        echo "You forgot your password!"
    fi
    exit 1
fi

# Now starting the script where the command is "start [password]"

# Checking to see if docker is running.
systemctl status docker || systemctl start docker
echo "Docker running."

# Checks to see if the container is running.
if [ "$(docker ps -f name=jrvs-psql | wc -l)" = "2" ]; 
then
    echo "Docker container is running!"
    exit 0
fi 

# If it isn't running, starts setting up.
# Check to see of the docker volume is created.
if [ "$(docker volume ls | wc -l)" != "2" ];
then
    docker volume create pgdata
    echo "Docker volume initialized!"
else 
    echo "Docker volume already exists!"
fi

# Checks if the container exists. If it doesn't, create it. If it does,
# then run the existing container image.
PGPASSWORD=$2

if [ "$(docker container ls -a -f name=jrvs-psql | wc -l)" != "2" ];
then
    docker run --name jrvs-psql -e POSTGRES_PASSWORD=$PGPASSWORD -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres
else
    docker container start jrvs-psql
echo "Docker container started!"
fi

sudo yum install -y postgresql
echo "PostgreSQL installed."
psql -h localhost -U postgres -W
echo "Logged into PostgreSQL."
postgres=#\l
echo "The above are the existing databases."

exit 0
