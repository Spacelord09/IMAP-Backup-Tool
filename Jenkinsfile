properties([pipelineTriggers([githubPush()])])
pipeline {
	agent any	
	stages {
		stage('Build') {
			steps {
				sh 'mvn -B -DskipTests clean package'
				sh 'rm -f -- target/original*'
				archiveArtifacts artifacts: 'target/*.jar', followSymlinks: false
			}
		}
	}
}
