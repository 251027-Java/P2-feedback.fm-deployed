/*
possibly useful references in the future
https://www.jenkins.io/blog/2020/04/16/github-app-authentication/#how-do-i-get-an-api-token-in-my-pipeline
https://plugins.jenkins.io/checks-api/
 */

def runPipeline = true
def checkNames = [
    lintFrontend: 'lint / frontend',
    testBackend: 'test / backend',
]

def limitText(text, end = true) {
    // https://github.com/jenkinsci/junit-plugin/blob/6c6699fb25df1b7bae005581d9af2ed698c47a4c/src/main/java/io/jenkins/plugins/junit/checks/JUnitChecksPublisher.java#L72
    // stay within limits of check api for summaries
    def MAX_MSG_SIZE_TO_CHECKS_API = 65535
    def limit = MAX_MSG_SIZE_TO_CHECKS_API - 1024

    if (end) {
        return text.substring(Math.max(0, text.length() - limit))
    }

    return text.take(limit)
}

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
                    /*
                    Using multibranch pipelines does not have the "origin/" part in env.GIT_BRANCH but
                    has env.BRANCH_IS_PRIMARY allowing for an easier check.
                    Keep the check for "origin/" in case we ever need to do testing with a regular pipeline.
                     */
                    def isDefaultOnPipeline = env.GIT_BRANCH == 'origin/' + env.GITHUB_DEFAULT_BRANCH
                    def isDefaultOnMultibranchPipeline = env.BRANCH_IS_PRIMARY == 'true'

                    if (isDefaultOnPipeline || isDefaultOnMultibranchPipeline) {
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

        stage('Frontend - Lint') {
            when {
                expression { runPipeline }
            }

            steps {
                withChecks(name: checkNames.lintFrontend) {
                    dir('frontend') {
                        script {
                            // https://biomejs.dev/recipes/continuous-integration/#gitlab-ci
                            docker.withRegistry('https://ghcr.io/', 'github-app-team') {
                                docker.image('biomejs/biome:latest').inside('--entrypoint=""') {
                                    def res = [con: 'SUCCESS', title: 'Success']

                                    try {
                                        sh 'biome ci --colors=off --reporter=summary > frontend-code-quality.txt'
                                    } catch (err) {
                                        res.con = 'FAILURE'
                                        res.title = 'Failed'
                                        throw err
                                    } finally {
                                        def output = readFile file: 'frontend-code-quality.txt'
                                        echo output

                                        publishChecks name: checkNames.lintFrontend,
                                            conclusion: res.con,
                                            summary: limitText(output),
                                            title: res.title
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        stage('Frontend - Dependencies') {
            when {
                expression { runPipeline }
            }

            steps {
                dir('frontend') {
                    script {
                        docker.image('node:lts-alpine').inside {
                            sh 'npm ci'
                            stash includes: 'node_modules/**', name: 'frontend-deps'
                        }
                    }
                }
            }
        }

        stage('Test') {
            when {
                expression { runPipeline }
            }

            steps {
                withChecks(name: checkNames.testBackend) {
                    dir('backend') {
                        sh './mvnw -B test'
                        junit '**/target/surefire-reports/TEST-*.xml'
                    }
                }
            }
        }
    }
}
