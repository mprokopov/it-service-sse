def repo = "663084659937.dkr.ecr.eu-central-1.amazonaws.com/it-service-sse"
def tag = "latest"
def image = "${repo}:${tag}"

pipeline {
    agent any

    stages{
        stage('Build jars') {
            steps{
                sh '/var/lib/jenkins/lein uberjar'
            }
        }
    stage('Build docker') {
      steps {
        script {
          tag = readFile('VERSION').trim() + "." + env.BUILD_NUMBER
          image = "${repo}:${tag}"

          docker.withRegistry("https://663084659937.dkr.ecr.eu-central-1.amazonaws.com", "ecr:eu-central-1:jenkins-aws-ecr") {
            docker.build(image).push()
          }
        }
      }
    }
  }
}
