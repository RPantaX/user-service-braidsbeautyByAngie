pipeline {
    agent any
    environment {
        DOCKER_HUB_REPO = 'rpantax/user-service'
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT.take(7)}"
        // Definir rama por defecto si no está disponible
        CURRENT_BRANCH = "${env.BRANCH_NAME ?: 'main'}"
    }
    tools{
        maven "Maven-3.9.10"
    }

    stages {
        stage('Clone Repo') {
            steps {
                echo "Checking out code from ${env.CURRENT_BRANCH} branch"
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/RPantaX/user-service-braidsbeautyByAngie.git',
                        credentialsId: 'github-token'
                    ]]
                ])
                script {
                    // Obtener información del commit y rama actual
                    env.GIT_COMMIT = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    env.CURRENT_BRANCH = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                    env.DOCKER_IMAGE_TAG = "${BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
                    echo "Building from branch: ${env.CURRENT_BRANCH}"
                    echo "Git commit: ${env.GIT_COMMIT}"
                    echo "Docker tag: ${env.DOCKER_IMAGE_TAG}"
                }
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Building Maven project...'
                sh 'mvn clean package -DskipTests -B'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                script {
                    // Construir imagen Docker
                    def dockerImage = docker.build("${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}")
                    env.DOCKER_IMAGE_ID = dockerImage.id

                    // Crear tags adicionales
                    dockerImage.tag("${env.CURRENT_BRANCH}-latest")

                    if (env.CURRENT_BRANCH == 'main') {
                        dockerImage.tag('latest')
                    }

                    echo "Docker image built successfully: ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG}"
                }
            }
        }

        stage('Docker Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                    // Agregar condición para cuando BRANCH_NAME sea null pero estemos en main
                    expression { env.CURRENT_BRANCH == 'main' }
                    expression { env.CURRENT_BRANCH == 'develop' }
                }
            }
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                script {
                            docker.withRegistry('https://index.docker.io/v1/', 'jenkins-cicd-token2') {
                                docker.image("${DOCKER_HUB_REPO}:latest").push()
                    }
                }
                echo "Docker image pushed successfully: ${DOCKER_HUB_REPO}:latest"
                    }
                }
        stage('k8s deployment') {
            steps {
                script {
                    // Reemplaza la imagen en el archivo de despliegue con el tag real generado
                    sh "sed -i 's|rpantax/user-service:latest|rpantax/user-service:${DOCKER_IMAGE_TAG}|' k8s-deploy.yml"
                }
                sh 'kubectl apply -f k8s-deploy.yml'
            }
        }
        stage('Cleanup') {
            steps {
                echo 'Cleaning up local Docker images...'
                script {
                    // Limpiar imágenes locales para ahorrar espacio
                    sh """
                        docker rmi ${DOCKER_HUB_REPO}:${DOCKER_IMAGE_TAG} || true
                        docker rmi ${DOCKER_HUB_REPO}:${env.CURRENT_BRANCH}-latest || true
                        if [ "${env.CURRENT_BRANCH}" = "main" ]; then
                            docker rmi ${DOCKER_HUB_REPO}:latest || true
                        fi
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline completed'
            // Limpiar workspace si es necesario
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}