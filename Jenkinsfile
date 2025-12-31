/*
possibly useful references in the future
https://www.jenkins.io/blog/2020/04/16/github-app-authentication/#how-do-i-get-an-api-token-in-my-pipeline
https://plugins.jenkins.io/checks-api/
 */

def isRelatedToPrimaryBranch = false
def forceRun = false
def skipRun = false

def chNames = [
    lintFrontend: 'lint / frontend',
    testBackend: 'test / backend',
    buildFrontend: 'build / frontend',
    buildBackend: 'build / backend',
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
                    // https://javadoc.jenkins-ci.org/hudson/scm/ChangeLogSet.html
                    def size = currentBuild.changeSets.size()

                    // size is 0 if a PR was just made
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
                        // allow some options for user to either skip or force a run via commit message
                        // [skip] has higher priority if they for some reason provide both [skip] and [run]
                        if (entry.msg =~ /(?i)\[skip\]/) {
                            echo '[skip] identified: skipping tests'
                            skipRun = true
                            return
                        } else if (entry.msg =~ /(?i)\[run\]/) {
                            echo '[run] identified: running all tests'
                            forceRun = true
                            return
                        }
                    }

                    /*
                    Using multibranch pipelines does not have the "origin/" part in env.GIT_BRANCH but
                    has env.BRANCH_IS_PRIMARY allowing for an easier check.
                    Keep the check for "origin/" in case we ever need to do testing with a regular pipeline.
                     */
                    def isDefaultOnPipeline = env.GIT_BRANCH == 'origin/' + env.GITHUB_DEFAULT_BRANCH
                    def isDefaultOnMultibranchPipeline = env.BRANCH_IS_PRIMARY == 'true'

                    if (isDefaultOnPipeline || isDefaultOnMultibranchPipeline) {
                        isRelatedToPrimaryBranch = true
                        echo 'default branch: running'
                        return
                    }

                    if (env.CHANGE_TARGET == env.GITHUB_DEFAULT_BRANCH) {
                        isRelatedToPrimaryBranch = true
                        echo 'PR to default branch: running'
                        return
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
                        expression { isRelatedToPrimaryBranch }
                        changeset '**/frontend/**'
                    }
                }
            }

            steps {
                withChecks(name: chNames.lintFrontend) {
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

                                        publishChecks name: chNames.lintFrontend,
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

        stage('test backend') {
            when {
                not { expression { skipRun } }
                anyOf {
                    expression { forceRun }
                    changeset 'Jenkinsfile'
                    allOf {
                        expression { isRelatedToPrimaryBranch }
                        changeset '**/backend/**'
                    }
                }
            }

            steps {
                withChecks(name: chNames.testBackend) {
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
                        expression { isRelatedToPrimaryBranch }
                        changeset '**/frontend/**'
                    }
                }
            }

            steps {
                withChecks(name: chNames.buildFrontend) {
                    dir('frontend') {
                        script {
                            docker.image('node:lts-alpine').inside {
                                def res = [con: 'SUCCESS', title: 'Success']

                                try {
                                    sh 'npm ci'
                                    sh 'npm run build'
                                } catch (err) {
                                    res.con = 'FAILURE'
                                    res.title = 'Failed'
                                    throw err
                                } finally {
                                    publishChecks name: chNames.buildFrontend,
                                        conclusion: res.con,
                                        title: res.title
                                }
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
                        expression { isRelatedToPrimaryBranch }
                        changeset '**/backend/**'
                    }
                }
            }

            steps {
                withChecks(name: chNames.buildBackend) {
                    dir('backend') {
                        script {
                            def res = [con: 'SUCCESS', title: 'Success']

                            try {
                                sh './mvnw -B package -DskipTests'
                            } catch (err) {
                                res.con = 'FAILURE'
                                res.title = 'Failed'
                                throw err
                            } finally {
                                publishChecks name: chNames.buildBackend,
                                    conclusion: res.con,
                                    title: res.title
                            }
                        }
                    }
                }
            }
        }
    }
}
