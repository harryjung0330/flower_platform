#!/bin/bash
touch /home/ec2-user/web-server.log
touch /home/ec2-user/web-server-error.log
sudo java -jar -Djasypt.encryptor.password=$(cat jasyptKey.txt) -Dspring.profiles.active=test -Dserver.port=80 flower-platform.jar >/home/ec2-user/web-server.log 2>/home/ec2-user/web-server-error.log &

