#!/bin/bash

sudo java -jar -Dlogging.info.path=/home/ec2-user/server.log -Dspring.datasource.url=$(cat /home/ec2-user/dbEndpoint.txt) -Djasypt.encryptor.password=$(cat /home/ec2-user/jasyptKey.txt) -Dspring.profiles.active=test -Dserver.port=80 /home/ec2-user/flower-platform.jar &