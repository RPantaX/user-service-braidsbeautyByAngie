pipeline {
    agent any
    environment {
        WORKSPACE = "/var/lib/jenkins/workspace/user-service"
        DOCKER_HUB_REPO = 'rpantax/user-service'
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT.take(7)}"
    }
    tools{
        maven "maven4.0.0"
    }

    stages {
        stage('Clone Repo') {
            // for display purposes
            // Get some code from a GitHub repository
            steps {
                git url: 'https://github.com/RPantaX/user-service-braidsbeautyByAngie.git',
                credentialsId: 'github-token',
                branch: 'main'
            }

        }
        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests -B'
            }
        }
        stage('Build docker') {
            steps {
                echo 'Building Docker image...'
                script {
                    def dockerImage = docker.build("${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}")
                    env.DOCKER_IMAGE_ID = dockerImage.id

                    if (env.BRANCH_NAME == 'main') {
                        dockerImage.tag('latest')
                    }
                    dockerImage.tag("${env.BRANCH_NAME}-latest")
                }
            }

        }

         stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                sh "docker build -t ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG} ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo "Pushing image to Docker Hub: ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}"
                script {
                    withCredentials([usernamePassword(credentialsId: 'jenkins-cicd-token2', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh """
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            docker push ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}
                        """
                    }
                }
            }
        }

        stage('Deploy Docker Image') {
            steps {
                echo "Running Docker Image: ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}"
                sh "docker stop user-service || true && docker rm user-service || true"
                sh "docker run --name user-service -d -p 8081:8081 ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}"
            }
        }

    }
}
