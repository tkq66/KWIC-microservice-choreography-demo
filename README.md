# KWIC-microservice-choreography-demo

This is a microservice implementation of Keywords in Context (KWIC), a popular software engineering case study. The microservice follows an choreography architecture. All services are buil using Spring with MongoDB as persistent storage and built with Docker and perform pub/sub through RabbitMQ.

## Services
There are 5 services:
1. **kwic-orchestration-service** - The main orchestrator, calling each services sequentially. File upload limit is 5GB. The REST request is done by Spring's WebFlux.
2. **chariwise-input-service** - An input service that takes in user data in the form of file upload, raw text, or JSON list and store them into a persistent storage.
3. **chairwise-circular-shift-service** - Performs circular shift over text data line-by-line to extrat all keywords. Data is provided by the *chairwise-input-service*, in which data from the database outputs into a file and *chairwise-circular-shift-service* then downloads that file into its own local filesystem before processing it. All data transfers in this architecture follow this pattern to avoid reading full data into memory.
4. **chairwise-sorting-service** - Performs alphabetical descending sorting over text data. Data is provided by *chairwise-circular-shift-service*, with data transfer scheme as described in the point above.
5. **chariwise-output-service** - Prepares and sends out different forms of ouput for the results. Outputs are provided in the form of: file download stream, file location string to be downloaded by the client, raw text, and JSON lis. Data is provided by *chairwise-sorting-service*, with data transfer scheme as described in the point above.

## Requirements
* Ubuntu or other Debiand OS
* Java 8
* Maven
* Docker
* Docker Compose
* Virtualbox
* Kubectl
* Minikube

## Deployment
This architecture can be deployed with either Docker Compose or Kubernetes. All deployment configurations are stored inside *src/build* of each project. To deploy each services individually, two Bash scripts are provided for both deployment methods: *build-with-docker.sh* and *build-with-kubernetes.sh*. Two Maven and Spring profiles are explicitly defined: *dev-docker* and *dev-kubernetes*. They may be separately activated during he Maven build stage for each deployment method.

### Docker Compose
Image of each service is build with multi-stage build and dependency caching to optimize build speed. A **bridge** network is explicitly defined for all services so that a service may be discoverable by all services that depend on it. The orchestration service have the longest list of networks, for example, because it needs to connect with all other services.

For convenience, *build-with-docker.sh* script is provided in the root folder to automatically deploy each service in a different gnome terminal, so you don't have to run the build script for each folder manually.

### Kubernetes
