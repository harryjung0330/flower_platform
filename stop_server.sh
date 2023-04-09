#!/bin/bash

if [[ "$(ss -tulpn | grep LISTEN | grep :80)" == '' ]]; then
        echo "no process running on port 80"
    else
        sudo curl -X POST http://localhost:80/actuator/shutdown
fi
