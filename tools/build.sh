#!/bin/sh
echo creating docker env...
cp ./app_properties/docker.properties ../src/main/resources/application.properties
echo docker env created
echo creating build server...
docker build -t zoode-backend ../
echo build was created
echo run compose servers...
docker-compose up -d
cp ./app_properties/dev.properties ../src/main/resources/application.properties
read -n 1 -s -r -p "Press any key to continue"
