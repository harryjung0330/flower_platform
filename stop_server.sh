#!/bin/bash

sudo curl -X POST http://localhost:80/actuator/shutdown || echo "failed to exit program"