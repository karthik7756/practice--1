pipeline {
  agent any
  environment { IMAGE_NAME = 'financial-transaction-app' }
  stages {
    stage('Checkout') { steps { checkout scm } }
    stage('Build & Test') { steps { sh 'mvn clean verify' } }
    stage('SonarQube') { steps { echo 'Configure SonarQube in Jenkins before enabling the scanner command.' } }
    stage('Docker Build') { steps { sh 'docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .'; sh 'docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:latest' } }
    stage('Trivy Scan') { steps { sh 'trivy image --exit-code 0 --severity HIGH,CRITICAL ${IMAGE_NAME}:${BUILD_NUMBER}' } }
    stage('Deploy') { steps { sh 'docker stop financial-app || true'; sh 'docker rm financial-app || true'; sh 'docker run -d --name financial-app -p 8080:8080 ${IMAGE_NAME}:latest' } }
  }
  post { always { junit 'target/surefire-reports/*.xml' } }
}
