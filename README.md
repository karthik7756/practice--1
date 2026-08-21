# Financial Transaction Service - DevSecOps on AWS EKS

An automated 11-stage CI/CD pipeline for a Java Spring Boot microservice deployed to Amazon EKS.

## 🚀 Pipeline Stages
1. **SCM Checkout**: GitHub master branch
2. **Secret Scan**: Gitleaks
3. **Build & Test**: Maven
4. **Code Analysis**: SonarQube SAST
5. **Quality Gate**: SonarQube Quality Gate
6. **Artifact Storage**: Sonatype Nexus
7. **Containerization**: Docker
8. **Vulnerability Scan**: Trivy
9. **Image Push**: Docker Hub
10. **Config Prep**: Ansible
11. **Kubernetes Deployment**: AWS EKS (LoadBalancer)

## 🛠️ Tools Used
- AWS (EKS, EC2, ELB)
- Jenkins, Docker, Kubernetes
- SonarQube, Nexus, Gitleaks, Trivy, Ansible
- Java, Spring Boot, Maven
