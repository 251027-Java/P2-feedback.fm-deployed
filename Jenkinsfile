/*
possibly useful references in the future
https://www.jenkins.io/blog/2020/04/16/github-app-authentication/#how-do-i-get-an-api-token-in-my-pipeline
https://plugins.jenkins.io/checks-api/
 */

def isPrToDefault = false
def isDefault = false
def forceRun = false
def skipRun = false

def fmChecks = [
    lint: [
        frontend: 'lint / frontend',
    ],
    test: [
        backend: 'test / backend',
    ],
    build: [
        frontend: 'build / frontend',
        backend: 'build / backend',
    ],
    docker: [
        frontend: 'docker / frontend',
        backend: 'docker / backend',
    ]
]

def buildSuccess = [
    frontend: false,
    backend: false,
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

        // disable colored output with vite
        NO_COLOR = 'true'
    }

    stages {
        stage('check run requirements') {
            steps {
                script {
                    sh 'printenv | sort'

                    /*
                    Using multibranch pipelines does not have the "origin/" part in env.GIT_BRANCH but
                    has env.BRANCH_IS_PRIMARY allowing for an easier check.
                    Keep the check for "origin/" in case we ever need to do testing with a regular pipeline.
                     */
                    isDefault = env.GIT_BRANCH == 'origin/' + env.GITHUB_DEFAULT_BRANCH || env.BRANCH_IS_PRIMARY == 'true'
                    isPrToDefault = env.CHANGE_TARGET == env.GITHUB_DEFAULT_BRANCH

                    // https://javadoc.jenkins-ci.org/hudson/scm/ChangeLogSet.html
                    def size = currentBuild.changeSets.size()

                    // check for changes for this event
                    if (size > 0) {
                        echo "change sets: ${size}"

                        // assuming this is git and we only ever have one scm
                        def changeSet = currentBuild.changeSets.first()
                        echo "${changeSet.kind}: commits: ${changeSet.items.size()}"

                        // only look at most recent commit
                        def entry = changeSet.items.last()
                        def date = new Date(entry.timestamp)

                        echo """
${entry.commitId}
${date.format('yyyy-MM-dd HH:mm:ss')} | ${entry.timestamp}
files changed: ${entry.affectedFiles.size()}
msg: ${entry.msg}
"""
                        // allow user to specify attributes for this run by checking the end of the
                        // commit message for [<attribute1>,<attribute2>,...]
                        def matcher = entry.msg =~ /\[([^\[]+)\]$/

                        if (matcher.find()) {
                            def attributes = (matcher.group(1).split(',').collect { it.trim() }) as Set

                            if (attributes.contains('skip')) {
                                echo '[skip]: skipping tests'
                                skipRun = true
                            } else if (attributes.contains('run')) {
                                echo '[run]: running all tests'
                                forceRun = true
                            }

                            if (attributes.contains('default')) {
                                echo '[default]: interpretting as default branch'
                                isDefault = true
                            } else if (attributes.contains('pr-default')) {
                                echo '[pr-default]: interpretting as a PR to default branch'
                                isPrToDefault = true
                            }

                            if (attributes.contains('docker-frontend')) {
                                echo '[docker-frontend]: will build the frontend docker image'
                                buildSuccess.frontend = true
                            }
                        }
                    }

                    if (isPrToDefault) {
                        // size = 0 when a PR is made. force a run
                        // https://github.com/251027-Java/P2-feedback.fm-deployed/issues/68
                        if (size == 0) {
                            echo 'PR made: running all tests'
                            forceRun = true
                        }

                        // https://github.com/251027-Java/P2-feedback.fm-deployed/issues/65
                        if (currentBuild.previousBuild?.result == 'FAILURE') {
                            echo 'previous run failed: running all tests'
                            forceRun = true
                        }
                    }
                }
            }
        }

        stage('lint frontend') {
            when {
                not { expression { skipRun } }
                anyOf {
                    expression { forceRun }
                    changeset 'Jenkinsfile'
                    allOf {
                        anyOf {
                            expression { isPrToDefault }
                            expression { isDefault }
                        }
                        changeset '**/frontend/**'
                    }
                }
                beforeAgent true
            }

            agent {
                // https://biomejs.dev/recipes/continuous-integration/#gitlab-ci
                docker {
                    args '--entrypoint=""'
                    image 'biomejs/biome:latest'
                    registryCredentialsId 'github-app-team'
                    registryUrl 'https://ghcr.io/'
                }
            }

            steps {
                withChecks(name: fmChecks.lint.frontend) {
                    dir('frontend') {
                        script {
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

                                publishChecks name: fmChecks.lint.frontend,
                                    conclusion: res.con,
                                    summary: limitText(output),
                                    title: res.title
                            }
                        }
                    }
                }
            }
        }

        stage('test backend') {
            when {
                not { expression { skipRun } }
                anyOf {
                    expression { forceRun }
                    changeset 'Jenkinsfile'
                    allOf {
                        anyOf {
                            expression { isPrToDefault }
                            expression { isDefault }
                        }
                        changeset '**/backend/**'
                    }
                }
            }

            steps {
                withChecks(name: fmChecks.test.backend) {
                    dir('backend') {
                        sh './mvnw -B test'
                        junit '**/target/surefire-reports/TEST-*.xml'
                    }
                }
            }
        }

        stage('build frontend') {
            when {
                not { expression { skipRun } }
                anyOf {
                    expression { forceRun }
                    changeset 'Jenkinsfile'
                    allOf {
                        anyOf {
                            expression { isPrToDefault }
                            expression { isDefault }
                        }
                        changeset '**/frontend/**'
                    }
                }
                beforeAgent true
            }

            agent {
                docker {
                    image 'node:lts-alpine'
                }
            }

            steps {
                withChecks(name: fmChecks.build.frontend) {
                    dir('frontend') {
                        script {
                            def res = [con: 'SUCCESS', title: 'Success']

                            try {
                                sh 'npm ci'
                                sh 'npm run build'

                                // build image and push to docker hub if on default branch
                                buildSuccess.frontend = isDefault
                            } catch (err) {
                                res.con = 'FAILURE'
                                res.title = 'Failed'
                                throw err
                            } finally {
                                publishChecks name: fmChecks.build.frontend,
                                    conclusion: res.con,
                                    title: res.title
                            }
                        }
                    }
                }
            }
        }

        stage('build backend') {
            when {
                not { expression { skipRun } }
                anyOf {
                    expression { forceRun }
                    changeset 'Jenkinsfile'
                    allOf {
                        anyOf {
                            expression { isPrToDefault }
                            expression { isDefault }
                        }
                        changeset '**/backend/**'
                    }
                }
            }

            steps {
                withChecks(name: fmChecks.build.backend) {
                    dir('backend') {
                        script {
                            def res = [con: 'SUCCESS', title: 'Success']

                            try {
                                sh './mvnw -B package -DskipTests'

                                // build image and push to docker hub if on default branch
                                buildSuccess.backend = isDefault
                            } catch (err) {
                                res.con = 'FAILURE'
                                res.title = 'Failed'
                                throw err
                            } finally {
                                publishChecks name: fmChecks.build.backend,
                                    conclusion: res.con,
                                    title: res.title
                            }
                        }
                    }
                }
            }
        }

        stage('docker frontend') {
            when {
                expression { buildSuccess.frontend }
            }

            steps {
                echo 'do something here frontend'

                publishChecks name: fmChecks.docker.frontend,
                    title: 'Pending',
                    status: 'IN_PROGRESS'

                dir('frontend') {
                    script {
                        def image = docker.build('minidomo/feedbackfm/frontend')

                        docker.withRegistry('https://index.docker.io/v2/', 'docker-hub-cred') {
                            image.push()
                            
                            def sha = env.GIT_COMMIT.take(7)
                            def buildTag = env.BUILD_TAG.substring('jenkins-'.length())
                            image.push("${buildTag}-${sha}")
                        }
                    }
                }
            }

            post {
                success {
                    publishChecks name: fmChecks.docker.frontend, conclusion: 'SUCCESS', title: 'Success'
                }

                failure {
                    publishChecks name: fmChecks.docker.frontend, conclusion: 'FAILURE', title: 'Failed'
                }
            }
        }

        stage('docker backend') {
            when {
                expression { buildSuccess.backend }
            }

            steps {
                echo 'do something here backend'
            }
        }
    }

    post {
        always {
            // delete the workspace after to prevent large disk usage
            cleanWs()
        }
    }
}
