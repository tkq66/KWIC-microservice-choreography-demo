#!/bin/sh

# Build the project
mvn clean install 
# Build Docker File
docker build -t siddsatish95/chairwise-input-service:test .;
# Push to DockerHub Registry
docker push siddsatish95/chairwise-input-service:test;
# Create Persistence Volume for DB
kubectl apply -f mongo-pvc.yaml
# Create Deployment mongodb
kubectl apply -f mongo-deployment.yaml
# Exposing Deployment through service to access from outside world
# kubectl port-forward deployment/mongodb 60001:27017 &
# Create Deployment chairwise-input-service
kubectl apply -f deployment.yml;
# Create service chairwise-input-service
kubectl apply -f service.yml;
# Obtain Cluster IP and port on localhost to which service is exposed to
# kubectl port-forward deployment/chairwise-input-service 60002:8080 &

# Obtain IP Address of Minikube
# minikube ip

# Now API can be accessed from URL
# http://<minikube_ip>:<service_port>/api/v1