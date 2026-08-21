pipeline {
    agent any

    environment {
        DOCKER_IMAGE   = "karthik7756/financial-transaction-app"
        SONAR_SERVER   = "sonar"
        NEXUS_IP       = "18.145.216.95:8081"
        NEXUS_REPO     = "maven-releases"
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('2. Security: Gitleaks Secret Scan') {
            steps {
                sh 'gitleaks detect --verbose --no-git --source . || true'
            }
        }

        stage('3. Maven Build & Unit Tests') {
            steps {
                sh 'mvn clean package -DskipTests=false'
            }
        }

        stage('4. SAST: SonarQube Code Analysis') {
            steps {
                withSonarQubeEnv("${SONAR_SERVER}") {
                    sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=financial-transaction-app'
                }
            }
        }

        stage('5. SonarQube Quality Gate') {
            steps {
                timeout(time: 3, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('6. Upload Artifact to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh '''
                        JAR_FILE=$(find target -name "*.jar" ! -name "*sources*" | head -n 1)
                        echo "Uploading ${JAR_FILE} to Nexus..."
                        curl -v -f -u ${NEXUS_USER}:${NEXUS_PASS} --upload-file ${JAR_FILE} \
                        "http://${NEXUS_IP}/repository/${NEXUS_REPO}/com/financial/financial-app/${BUILD_NUMBER}/financial-app-${BUILD_NUMBER}.jar"
                    '''
                }
            }
        }

        stage('7. Docker Build') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} -t ${DOCKER_IMAGE}:latest .'
            }
        }

        stage('8. Security: Trivy Container Scan') {
            steps {
                sh 'trivy image --severity HIGH,CRITICAL --exit-code 0 ${DOCKER_IMAGE}:${BUILD_NUMBER}'
            }
        }

        stage('9. Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Build, Gitleaks, SonarQube, Nexus, Trivy, and Docker Push Succeeded!'
        }
    }
}
