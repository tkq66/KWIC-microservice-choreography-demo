kubectl apply -f mongo-pvc.yaml
kubectl apply -f mongo-deployment.yaml
kubectl apply -f mongo-service.yaml
kubectl port-forward deployment/mongodb 27017:27017
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl port-forward deployment/chairwise-input-service 8080:8080