#!/bin/sh
docker run -d --name zoode-db -p 5434:5432 -e POSTGRES_PASSWORD=defaultpassword -e POSTGRES_USER=defaultuser -e POSTGRES_DB=zoode-db postgres:alpine
sleep 2
read -n 1 -s -r -p "Press any key to continue"
