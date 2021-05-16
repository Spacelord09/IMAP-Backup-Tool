properties([pipelineTriggers([githubPush()])])
pipeline {
	agent any	
	stages {
		stage('Install-Dependencies') {
			steps {
				sh 'DEBIAN_FRONTEND=noninteractive apt-get update -y -qq > /dev/null'
				sh 'DEBIAN_FRONTEND=noninteractive apt-get install -y -qq --no-install-suggests --no-install-recommends maven > /dev/null'
			}
		}
		stage('Build') {
			steps {
				sh 'mvn -B -DskipTests clean package'
				sh 'rm -f -- target/original*'
				archiveArtifacts artifacts: 'target/*.jar', followSymlinks: false
			}
		}
	}
}
