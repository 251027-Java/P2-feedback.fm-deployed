pipeline {
    agent any

    tools {
        jdk 'java25'
    }

    stages {
        stage('Simple') {
            steps {
                echo "Build number: ${env.BUILD_NUMBER}"
                echo "Building on: ${env.NODE_NAME}"
                sh 'printenv | sort'
            }
        }
    }

    post {
        success {
            echo 'Pipeline success'
        }
        failure {
            echo 'Pipeline failed'
        }
    }
}
