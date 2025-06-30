pipeline {
    agent any
    environment {
        WORKSPACE = "/var/lib/jenkins/workspace/user-service"
        dockerImageTag = "user-service${env.BUILD_NUMBER}"
    }
    tools{
        maven "Maven-3.9.9"
    }

    stages {
        stage('Clone Repo') {
            // for display purposes
            // Get some code from a GitHub repository
            git url: 'https://github.com/RPantaX/user-service-braidsbeautyByAngie.git',
                credentialsId: 'user-service',
                branch: 'main'
        }
        stage('Maven Build') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Build docker') {
            dockerImage = docker.build("user-service:${env.BUILD_NUMBER}")
        }

         stage('Deploy docker'){
            echo "Docker Image Tag Name: ${dockerImageTag}"
                  sh "docker stop user-service || true && docker rm user-service || true"
                  sh "docker run --name springboot-deploy -d -p 8081:8081 springboot-deploy:${env.BUILD_NUMBER}"
        }
    }
}
