/*
possibly useful references in the future
https://www.jenkins.io/blog/2020/04/16/github-app-authentication/#how-do-i-get-an-api-token-in-my-pipeline
https://plugins.jenkins.io/checks-api/
 */

def runPipeline = true

pipeline {
    agent any

    tools {
        jdk 'java25'
    }

    environment {
        GITHUB_OWNER = '251027-Java'
        GITHUB_REPO = 'P2-feedback.fm-deployed'
        GITHUB_DEFAULT_BRANCH = 'main'
    }

    stages {
        stage('Info') {
            steps {
                sh 'printenv | sort'
            }
        }

        stage('Check run requirements') {
            steps {
                script {
                    if (env.GIT_BRANCH == 'origin/' + env.GITHUB_DEFAULT_BRANCH) {
                        echo 'This is the default branch. Running.'
                        return
                    }

                    if (env.CHANGE_TARGET == env.GITHUB_DEFAULT_BRANCH) {
                        echo 'This is a PR to the default branch. Running'
                        return
                    }

                    runPipeline = false
                    echo "Does not meet the requirements to run: ${env.GIT_COMMIT}"
                }
            }
        }

        stage('Test') {
            when {
                expression { runPipeline }
            }

            steps {
                dir('backend') {
                    withChecks(name: 'Maven Tests', includeStage: true) {
                        sh './mvnw -B test'
                        junit '**/target/surefire-reports/TEST-*.xml'
                    }
                }
            }
        }
    }
}
