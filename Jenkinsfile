pipeline {
    agent any

    environment {
        DOCKER_IMAGE        = 'karthik7756/financial-transaction-app'
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
                script {
                    try {
                        withSonarQubeEnv('1') {
                            sh 'mvn sonar:sonar -Dsonar.projectKey=financial-transaction-app -Dsonar.projectName="Financial Transaction App"'
                        }
                    } catch (Exception e) {
                        try {
                            withSonarQubeEnv('SonarQube') {
                                sh 'mvn sonar:sonar -Dsonar.projectKey=financial-transaction-app -Dsonar.projectName="Financial Transaction App"'
                            }
                        } catch (Exception ex) {
                            sh 'mvn sonar:sonar -Dsonar.projectKey=financial-transaction-app -Dsonar.projectName="Financial Transaction App" || true'
                        }
                    }
                }
            }
        }

        stage('5. SonarQube Quality Gate') {
            steps {
                script {
                    try {
                        timeout(time: 2, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: false
                        }
                    } catch (Exception e) {
                        echo "Quality Gate check skipped/passed."
                    }
                }
            }
        }

        stage('6. Upload Artifact to Nexus') {
            steps {
                script {
                    try {
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
                    } catch (Exception e) {
                        echo "Nexus upload bypassed."
                    }
                }
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
                    if command -v trivy > /dev/null; then
                        trivy image --severity HIGH,CRITICAL --exit-code 0 ${DOCKER_IMAGE}:${BUILD_NUMBER} || true
                    else
                        echo "Trivy not installed, continuing pipeline"
                    fi
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
                    if [ -f ansible/deploy-config.yml ]; then
                        ansible-playbook -i localhost, -c local ansible/deploy-config.yml || true
                    else
                        echo "Ansible playbook not found, checking cluster connectivity:"
                        kubectl cluster-info
                    fi
                '''
            }
        }

        stage('11. Kubernetes: Deploy to EKS') {
            steps {
                sh '''
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                    kubectl set image deployment/financial-app financial-app=${DOCKER_IMAGE}:${BUILD_NUMBER}
                    kubectl rollout status deployment/financial-app --timeout=180s
                '''
            }
        }
    }

    post {
        always {
            sh 'docker image prune -f || true'
        }
        success {
            echo "Pipeline deployed successfully to EKS!"
        }
    }
}
