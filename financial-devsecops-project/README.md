# Financial Transaction Management System

Demo financial transaction application for a DevSecOps project.

## Features
- Health endpoint
- Account listing and lookup
- Demo account-to-account transfer
- Transaction history
- H2 database
- Docker, Jenkins and Kubernetes support

## Stack
Java 17, Spring Boot, Maven, H2, GitHub, Jenkins, SonarQube, OWASP Dependency-Check, Nexus, Docker, Trivy, Kubernetes, AWS EC2, Prometheus and Grafana.

## Run
mvn clean package
mvn spring-boot:run

Endpoints:
GET /api/health
GET /api/accounts
GET /api/accounts/{accountNumber}
POST /api/transactions/transfer
GET /api/transactions/history/{accountNumber}

Transfer JSON:
{"fromAccount":"100001","toAccount":"100002","amount":5000}

This is a learning/demo application. It does not process real money or connect to banking/payment networks.
