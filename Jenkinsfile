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
        stage('Check run requirements') {
            steps {
                script {
                    if (GIT_BRANCH == 'origin/' + GITHUB_DEFAULT_BRANCH) {
                        echo 'This is the default branch. Running.'
                        return
                    }

                    echo 'Checking if this has an open PR that is targetting the default branch'

                    if (CHANGE_TARGET == 'main') {
                        echo 'This is an PR to the default branch. Running'
                        return
                    }

                    /*
                    // old PR check code
                    // tried the URL below but doesn't work
                    // https://www.jenkins.io/blog/2020/04/16/github-app-authentication/#how-do-i-get-an-api-token-in-my-pipeline

                    // try separate PAT instead
                    // maybe manually create my own installation access token:
                    // https://docs.github.com/en/apps/creating-github-apps/authenticating-with-a-github-app/generating-an-installation-access-token-for-a-github-app
                    def response = httpRequest authentication: 'github-pat',
                        url: "https://api.github.com/repos/${GITHUB_OWNER}/${GITHUB_REPO}/commits/${GIT_COMMIT}/pulls",
                        customHeaders: [
                            [name: 'X-GitHub-Api-Version', value: '2022-11-28'],
                            [name: 'Accept', value: 'application/vnd.github+json']
                        ]

                    if (response.status != 200) {
                        error "Unsuccess API call for PRs: Code ${response.status}"
                    }

                    def prList = readJSON text: response.content
                    def validOpen = prList.any { it.state == 'open' && it.base.ref == GITHUB_DEFAULT_BRANCH }

                    if (validOpen) {
                        echo "Commit has an open PR to ${GITHUB_DEFAULT_BRANCH}"
                        return
                    }
                    */

                    currentBuild.result = 'ABORTED'
                    error "Does not meet the requirements to run: ${GIT_COMMIT}"
                }
            }
        }

        stage('Info') {
            steps {
                publishChecks(name: 'Build Info', title: 'Build Info', summary: 'Running', status: 'IN_PROGRESS')

                echo "Build tag: ${env.BUILD_TAG}"
                echo "Branch: ${GIT_BRANCH}"
                echo "Commit: ${GIT_COMMIT}"

                // for debugging. remove this later
                sh 'printenv | sort'

                publishChecks(name: 'Build Info', title: 'Build Info', summary: 'Success', status: 'COMPLETED', conclusion: 'SUCCESS')
            }
        }

        stage('Test') {
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
