#!/bin/bash

sudo java -jar -Djasypt.encryptor.password=$(cat /home/ec2-user/jasypt_key.txt) -Dspring.profiles.active=test -Dserver.port=80 /home/ec2-user/flower-platform.jar >/home/ec2-user/web-server.log 2>/home/ec2-user/web-server-error.log &

