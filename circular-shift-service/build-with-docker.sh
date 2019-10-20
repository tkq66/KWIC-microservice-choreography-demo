#!/bin/bash

# aggregate temp files
aggregate_files () {
	cp src/build/docker/docker-compose.yml .;
	cp src/build/docker/Dockerfile .;
}

# cleanup temp files
cleanup() {
	rm docker-compose.yml;
	rm Dockerfile;
}

# this function is called when Ctrl-C is sent
trap_ctrlc () {
    # perform cleanup here
    cleanup; 
    # exit shell script with error code 2
    # if omitted, shell script will continue execution
    exit 2;
}
# initialise trap to call trap_ctrlc function
# when signal 1 (SIGHUP), 2 (SIGINT), 3 (SIGQUIT),
#			  9 (SIGKILL), or 15 (SIGTERM) is received
trap "trap_ctrlc" 1;
trap "trap_ctrlc" 2;
trap "trap_ctrlc" 3;
trap "trap_ctrlc" 9;
trap "trap_ctrlc" 15;
 
aggregate_files;
sudo docker-compose up --build --remove-orphans;
cleanup;
