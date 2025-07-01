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
                echo "Checking out code from ${env.BRANCH_NAME} branch"
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
        stage('Docker Build') {
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
        stage('Docker Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'jenkins-cicd-token2') {
                        def image = docker.image("${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}")
                        image.push()
                        image.push("${env.BRANCH_NAME}-latest")

                        if (env.BRANCH_NAME == 'main') {
                            image.push('latest')
                        }
                    }
                }
                echo "Docker image pushed successfully: ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}"
            }
        }


    }
}
