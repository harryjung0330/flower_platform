#!/bin/bash
chmod 600 /home/ec2-user/jasyptKey.txt
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -s -m ec2 -c ssm:/test/web-server/config/cloudwatch-config.json
