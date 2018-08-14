pipeline {
    agent any

    stages {
        stage("Build") {
            steps {
                sh 'Hello World'
            }
        }
    }
    post {
        always {
            sh 'Hello World'
        }
    }
}