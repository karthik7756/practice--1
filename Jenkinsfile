pipeline {

    agent any

    environment {
        DOCKER_IMAGE = 'karthik7756/financial-transaction-app'
        SONAR_PROJECT = 'financial-transaction-app'
    }

    stages {

        stage('Checkout') {
            steps {
                echo '=== CHECKOUT FROM GITHUB ==='
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo '=== MAVEN BUILD ==='
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo '=== RUNNING TESTS ==='
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo '=== SONARQUBE ANALYSIS ==='

                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT} \
                        -Dsonar.projectName=${SONAR_PROJECT}
                    '''
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo '=== DOCKER BUILD ==='

                sh '''
                    docker build -t ${DOCKER_IMAGE}:latest .
                '''
            }
        }

        stage('Docker Push') {
            steps {
                echo '=== DOCKER HUB PUSH ==='

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login \
                            --username "$DOCKER_USER" \
                            --password-stdin

                        docker push ${DOCKER_IMAGE}:latest

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                echo '=== DEPLOYING CONTAINER ON EC2 ==='

                sh '''
                    docker pull ${DOCKER_IMAGE}:latest

                    docker rm -f financial-app || true

                    docker run -d \
                        --name financial-app \
                        -p 8080:8080 \
                        ${DOCKER_IMAGE}:latest

                    echo "=== CONTAINER STATUS ==="
                    docker ps

                    echo "=== DEPLOYMENT COMPLETED ==="
                '''
            }
        }
    }

    post {

        success {
            echo '========================================'
            echo '       CI/CD PIPELINE SUCCESSFUL'
            echo '========================================'
        }

        failure {
            echo '========================================'
            echo '        CI/CD PIPELINE FAILED'
            echo '========================================'
        }
    }
}
