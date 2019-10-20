#!/bin/bash

sudo gnome-terminal --title="KWIC Service" -- sh -c "cd kwic-service; sh build-with-docker.sh; bash";
sudo gnome-terminal --title="KWIC - Input Service" -- sh -c "cd input-service; sh build-with-docker.sh; bash";
sudo gnome-terminal --title="KWIC - Circular Shift Service" -- sh -c "cd circular-shift-service; sh build-with-docker.sh; bash";
sudo gnome-terminal --title="KWIC - Sorting Service" -- sh -c "cd sorting-service; sh build-with-docker.sh; bash";
