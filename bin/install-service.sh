#!/bin/sh

# install the docker container image as service and run it
sudo mkdir /opt/practicedates
sudo cp practicedates /opt/practicedates
sudo cp practicedates.service /etc/systemd/system/
sudo systemctl start practicedates.service
sudo systemctl enable practicedates.service

