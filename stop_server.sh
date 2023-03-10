#!/bin/bash

sudo kill $(sudo netstat -anp --numeric-ports | grep ":80\>.*:" | grep -o "[0-9]*/" | sed 's+/$++') || echo 'no server running'
