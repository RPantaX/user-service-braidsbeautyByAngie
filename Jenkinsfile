node {
	def WORKSPACE = "/var/lib/jenkins/workspace/user-service"
    def dockerImageTag = "user-service${env.BUILD_NUMBER}"

    try{
		//          notifyBuild('STARTED')
         stage('Clone Repo') {
			// for display purposes
            // Get some code from a GitHub repository
            git url: 'https://github.com/RPantaX/user-service-braidsbeautyByAngie.git',
                credentialsId: 'user-service',
                branch: 'main'
         }
          stage('Build docker') {
			dockerImage = docker.build("user-service:${env.BUILD_NUMBER}")
          }

          stage('Deploy docker'){
			echo "Docker Image Tag Name: ${dockerImageTag}"
                  sh "docker stop user-service || true && docker rm user-service || true"
                  sh "docker run --name springboot-deploy -d -p 8081:8081 springboot-deploy:${env.BUILD_NUMBER}"
          }
    }catch(e){
		//         currentBuild.result = "FAILED"
        throw e
    }finally{
		//         notifyBuild(currentBuild.result)
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
