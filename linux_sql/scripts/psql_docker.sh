#!/bin/bash


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

systemctl status docker || system start docker
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
if [ "$docker container ls -a -f name=jrvs-psql | wc -l)" != "2" ];
then
    docker run --name jrvs-psql -e POSTGRES_PASSWORD=$2 -d -v pgdata:/var/lib/postgresql/data -p 5432:5432 postgres
else
    docker container start jrvs-psql
echo "Docker container running!"
fi

exit 0
