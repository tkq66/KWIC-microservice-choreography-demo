#!/bin/bash

cp src/build/kubernetes/deployment.yml .;
cp src/build/kubernetes/service.yml .;
cp src/build/kubernetes/Dockerfile .;

# Perform build and deployment

rm deployment.yml;
rm service.yml;
rm Dockerfile;