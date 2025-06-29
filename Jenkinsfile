pipeline {
	agent any

    environment {
		DOCKER_HUB_REPO = 'rpantax/user-service'
        DOCKER_IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT.take(7)}"

        // GitHub Authentication - FIXED
        GITHUB_USERNAME = 'RPantaX'
    }

    tools {
		maven 'Maven-4.0.0'
        jdk 'Java-17'  // FIXED: Cambiar a Java-21 que es lo que tienes instalado
    }

    stages {
		stage('Checkout') {
			steps {
				echo "Checking out code from ${env.BRANCH_NAME} branch"
                checkout scm
                script {
					env.GIT_COMMIT = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    env.GIT_BRANCH = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                }
            }
        }
		stage('Clone Repo') {
			steps {
				// for display purposes
				// Get some code from a GitHub repository
				git url: 'https://github.com/RPantaX/user-service-braidsbeautyByAngie.git',
					credentialsId: 'github-user',
					branch: 'main'

				}
        }
        stage('Build docker') {
			dockerImage = docker.build("user-service:${env.BUILD_NUMBER}")
        }

        stage('Deploy docker'){
			echo "Docker Image Tag Name: ${DOCKER_IMAGE_TAG}"
                  sh "docker stop user-service || true && docker rm springboot-deploy || true"
                  sh "docker run --name user-service -d -p 8082:8082 user-service:${env.BUILD_NUMBER}"
        }


    }
}
def notifyBuild(String buildStatus = 'STARTED'){

	// build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESSFUL'
  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def now = new Date()
  // message
  def subject = "${buildStatus}, Job: ${env.JOB_NAME} FRONTEND - Deployment Sequence: [${env.BUILD_NUMBER}] "
  def summary = "${subject} - Check On: (${env.BUILD_URL}) - Time: ${now}"
  def subject_email = "Spring boot Deployment"
  def details = """<p>${buildStatus} JOB </p>
    <p>Job: ${env.JOB_NAME} - Deployment Sequence: [${env.BUILD_NUMBER}] - Time: ${now}</p>
    <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME}</a>"</p>"""


  // Email notification
    emailext (
         to: "pantajefferson173@gmail.com",
         subject: subject_email,
         body: details,
         recipientProviders: [[$class: 'DevelopersRecipientProvider']]
       )
}
