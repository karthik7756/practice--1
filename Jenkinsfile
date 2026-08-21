pipeline {
    agent any

    tools {
        maven 'Default Maven'
    }

    environment {
        DOCKER_IMAGE        = 'karthik7756/financial-transaction-app'
        SCANNER_HOME        = tool 'SonarScanner'
        NEXUS_VERSION       = 'nexus3'
        NEXUS_PROTOCOL      = 'http'
        NEXUS_URL           = '54.176.45.136:8081'
        NEXUS_REPOSITORY    = 'maven-releases'
        NEXUS_CREDENTIAL_ID = 'nexus-creds'
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('2. Security: Gitleaks Secret Scan') {
            steps {
                sh '''
                    if command -v gitleaks > /dev/null; then
                        gitleaks detect --source . --verbose --no-git || true
                    else
                        echo "Gitleaks not found, skipping secret scan"
                    fi
                '''
            }
        }

        stage('3. Maven Build & Unit Tests') {
            steps {
                sh 'mvn clean package -DskipTests=false'
            }
        }

        stage('4. SAST: SonarQube Code Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                          -Dsonar.projectKey=financial-transaction-app \
                          -Dsonar.projectName='Financial Transaction App'
                    '''
                }
            }
        }

        stage('5. SonarQube Quality Gate') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('6. Upload Artifact to Nexus') {
            steps {
                nexusArtifactUploader(
                    nexusVersion: "${NEXUS_VERSION}",
                    protocol: "${NEXUS_PROTOCOL}",
                    nexusUrl: "${NEXUS_URL}",
                    groupId: 'com.example',
                    version: "${BUILD_NUMBER}",
                    repository: "${NEXUS_REPOSITORY}",
                    credentialsId: "${NEXUS_CREDENTIAL_ID}",
                    artifacts: [
                        [artifactId: 'financial-transaction-app',
                         classifier: '',
                         file: 'target/financial-transaction-app-1.0.0.jar',
                         type: 'jar']
                    ]
                )
            }
        }

        stage('7. Docker Build') {
            steps {
                sh '''
                    docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .
                    docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest
                '''
            }
        }

        stage('8. Security: Trivy Container Scan') {
            steps {
                sh '''
                    trivy image --severity HIGH,CRITICAL --exit-code 0 ${DOCKER_IMAGE}:${BUILD_NUMBER}
                '''
            }
        }

        stage('9. Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                        docker push ${DOCKER_IMAGE}:latest
                    '''
                }
            }
        }

        stage('10. Ansible: Node & Environment Prep') {
            steps {
                sh '''
                    ansible-playbook -i localhost, -c local ansible/deploy-config.yml
                '''
            }
        }

        stage('11. Kubernetes: Deploy to EKS') {
            steps {
                sh '''
                    sed -i "s|${DOCKER_IMAGE}:.*|${DOCKER_IMAGE}:${BUILD_NUMBER}|g" k8s/deployment.yaml
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                    kubectl rollout status deployment/financial-app-deployment --timeout=180s
                '''
            }
        }
    }

    post {
        always {
            sh '''
                docker image prune -f || true
            '''
        }
        success {
            echo "Pipeline completed successfully! Application deployed to AWS EKS."
        }
        failure {
            echo "Pipeline failed. Please check stage logs."
        }
    }
}
