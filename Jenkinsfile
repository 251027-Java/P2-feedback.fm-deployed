/*
possibly useful references in the future
https://www.jenkins.io/blog/2020/04/16/github-app-authentication/#how-do-i-get-an-api-token-in-my-pipeline
https://plugins.jenkins.io/checks-api/
 */

def fbfm = [
    changes: [
        frontend: false,
        backend: false,
        jenkinsfile: false,
    ],
    run: [
        force: false,
        skip: false,
    ],
    isDefault: false,
    isPrToDefault: false,
]

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
]

def buildSuccess = [
    frontend: false,
    backend: false,
]

def limitText = { text, end = true ->
    // https://github.com/jenkinsci/junit-plugin/blob/6c6699fb25df1b7bae005581d9af2ed698c47a4c/src/main/java/io/jenkins/plugins/junit/checks/JUnitChecksPublisher.java#L72
    // stay within limits of check api for summaries
    def MAX_MSG_SIZE_TO_CHECKS_API = 65535
    def limit = MAX_MSG_SIZE_TO_CHECKS_API - 1024

    if (end) {
        return text.substring(Math.max(0, text.length() - limit))
    }

    return text.take(limit)
}

def shortSha = { ->
    return env.GIT_COMMIT.take(7)
}

def checkForChanges(ref, fbfm) {
    def cmd = ".jenkins/scripts/changes-count.sh ${ref}"
    fbfm.changes.frontend = sh(returnStdout: true, script: "${cmd} '^frontend'").trim() != '0'
    fbfm.changes.backend = sh(returnStdout: true, script: "${cmd} '^backend'").trim() != '0'
    fbfm.changes.jenkinsfile = sh(returnStdout: true, script: "${cmd} '^Jenkinsfile'").trim() != '0'
}

def handleFileChanges(fbfm) {
    // https://javadoc.jenkins-ci.org/hudson/scm/ChangeLogSet.html
    def size = currentBuild.changeSets.size()

    // basically checking if there were changes for this event
    if (size > 0) {
        // assuming this is git and we only ever have one scm
        def changeSet = currentBuild.changeSets.first()
        echo "${changeSet.kind}: commits: ${changeSet.items.size()}"

        // check changes relative to the last `N` commits since a push can have multiple commits
        checkForChanges("HEAD~${changeSet.items.size()}", fbfm)
    }

    // should take care of the following issues
    // https://github.com/251027-Java/P2-feedback.fm-deployed/issues/68
    // https://github.com/251027-Java/P2-feedback.fm-deployed/issues/65
    // check changes relative to the default branch
    // PR creation has a size of 0
    if (fbfm.isPrToDefault && (size == 0 || currentBuild.previousBuild?.result == 'FAILURE')) {
        checkForChanges(env.GITHUB_DEFAULT_BRANCH, fbfm)
    }
}

def handleCommitAttributes = { ->
    if (currentBuild.changeSets.size() > 0) {
        def message = sh(returnStdout: true, script: '.jenkins/scripts/commit-message.sh').trim()
        echo "commit message: ${message}"

        // allow user to specify attributes for this run by checking the commit message for
        // [<attribute1>,<attribute2>,...]
        def matcher = message =~ /\[([^\[]+)\]/

        if (matcher.find()) {
            def attributes = (matcher.group(1).split(',').collect { it.trim() }) as Set
            echo "attributes: ${attributes}"

            if (attributes.contains('skip')) {
                echo '[skip]: skipping tests'
                fbfm.run.skip = true
            } else if (attributes.contains('run')) {
                echo '[run]: running all tests'
                fbfm.run.force = true
            }

            if (attributes.contains('default')) {
                echo '[default]: interpretting as default branch'
                fbfm.isDefault = true
            } else if (attributes.contains('pr-default')) {
                echo '[pr-default]: interpretting as a PR to default branch'
                fbfm.isPrToDefault = true
            }

            // build images based on "image-*"
            buildSuccess.each { k, v ->
                // these template strings are type GString, so convert to string
                // to work with Set<String>.contains()
                def target = "image-${k}".toString()

                if (attributes.contains(target)) {
                    echo "[${target}]: will build the ${k} image"
                    buildSuccess[k] = true
                }
            }
        }
    }
}

def markStageFailure = { ->
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        sh 'this will fail'
    }
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

                    currentBuild.displayName = "${currentBuild.displayName} ${shortSha()}"

                    /*
                    Using multibranch pipelines does not have the "origin/" part in env.GIT_BRANCH but
                    has env.BRANCH_IS_PRIMARY allowing for an easier check.
                    Keep the check for "origin/" in case we ever need to do testing with a regular pipeline.
                     */
                    fbfm.isDefault = env.BRANCH_IS_PRIMARY == 'true' || env.GIT_BRANCH == 'origin/' + env.GITHUB_DEFAULT_BRANCH
                    fbfm.isPrToDefault = env.CHANGE_TARGET == env.GITHUB_DEFAULT_BRANCH

                    checkForChanges('HEAD~1', fbfm)

                    echo "${fbfm.changes}"

                    echo 'after check for changes call direct'

                    handleFileChanges(fbfm)
                    handleCommitAttributes()
                }
            }
        }

        stage('test') {
            steps {
                script {
                    markStageFailure()
                    echo 'does this work'
                }
            }
        }

        stage('lint frontend') {
            when {
                not { expression { fbfm.run.skip } }
                anyOf {
                    expression { fbfm.run.force }
                    allOf {
                        anyOf {
                            expression { fbfm.isPrToDefault }
                            expression { fbfm.isDefault }
                        }
                        anyOf {
                            expression { fbfm.changes.frontend }
                            expression { fbfm.changes.jenkinsfile }
                        }
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
                publishChecks name: fmChecks.lint.frontend, title: 'Pending', status: 'IN_PROGRESS'

                dir('frontend') {
                    script {
                        def res = null

                        try {
                            sh 'biome ci --colors=off --reporter=summary > frontend-code-quality.txt'
                            res = [con: 'SUCCESS', title: 'Success']
                        } catch (err) {
                            markStageFailure()
                            res = [con: 'FAILURE', title: 'Failed']
                            echo err
                        } finally {
                            def output = readFile file: 'frontend-code-quality.txt'
                            echo output

                            publishChecks name: fmChecks.lint.frontend, conclusion: res.con, title: res.title,
                                summary: limitText(output)
                        }
                    }
                }
            }
        }

        stage('test backend') {
            when {
                not { expression { fbfm.run.skip } }
                anyOf {
                    expression { fbfm.run.force }
                    allOf {
                        anyOf {
                            expression { fbfm.isPrToDefault }
                            expression { fbfm.isDefault }
                        }
                        anyOf {
                            expression { fbfm.changes.backend }
                            expression { fbfm.changes.jenkinsfile }
                        }
                    }
                }
            }

            steps {
                withChecks(name: fmChecks.test.backend) {
                    dir('backend') {
                        script {
                            try {
                                sh './mvnw -B test'
                                junit '**/target/surefire-reports/TEST-*.xml'
                            } catch (err) {
                                markStageFailure()
                                echo err
                            }
                        }
                    }
                }
            }
        }

        stage('build frontend') {
            when {
                not { expression { fbfm.run.skip } }
                anyOf {
                    expression { fbfm.run.force }
                    allOf {
                        anyOf {
                            expression { fbfm.isPrToDefault }
                            expression { fbfm.isDefault }
                        }
                        anyOf {
                            expression { fbfm.changes.frontend }
                            expression { fbfm.changes.jenkinsfile }
                        }
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
                publishChecks name: fmChecks.build.frontend, title: 'Pending', status: 'IN_PROGRESS'

                dir('frontend') {
                    script {
                        try {
                            sh 'npm ci && npm run build'
                        } catch (err) {
                            markStageFailure()
                            echo err
                        }
                    }
                }
            }

            post {
                success {
                    publishChecks name: fmChecks.build.frontend, conclusion: 'SUCCESS', title: 'Success'

                    script {
                        buildSuccess.frontend = fbfm.isDefault
                    }
                }

                failure {
                    publishChecks name: fmChecks.build.frontend, conclusion: 'FAILURE', title: 'Failed'
                }
            }
        }

        stage('build backend') {
            when {
                not { expression { fbfm.run.skip } }
                anyOf {
                    expression { fbfm.run.force }
                    allOf {
                        anyOf {
                            expression { fbfm.isPrToDefault }
                            expression { fbfm.isDefault }
                        }
                        anyOf {
                            expression { fbfm.changes.backend }
                            expression { fbfm.changes.jenkinsfile }
                        }
                    }
                }
            }

            steps {
                publishChecks name: fmChecks.build.backend, title: 'Pending', status: 'IN_PROGRESS'

                dir('backend') {
                    sh './mvnw -B package -DskipTests'
                }
            }

            post {
                success {
                    publishChecks name: fmChecks.build.backend, conclusion: 'SUCCESS', title: 'Success'

                    script {
                        buildSuccess.backend = fbfm.isDefault
                    }
                }

                failure {
                    publishChecks name: fmChecks.build.backend, conclusion: 'FAILURE', title: 'Failed'
                }
            }
        }

        stage('docker frontend') {
            when {
                expression { buildSuccess.frontend }
            }

            steps {
                echo 'asd'
            // build job: 'image', parameters: [
            //     booleanParam(name: 'IMG_PUSH_LATEST', value: true),
            //     string(name: 'IMG_COMMIT', value: 'asd'),
            //     string(name: 'IMG_TAG_SERIES', value: 'fe'),
            //     string(name: 'IMG_DIRECTORY', value: 'frontend'),
            // ]
            }
        }

        stage('docker backend') {
            when {
                expression { buildSuccess.backend }
            }

            steps {
                // TODO: implement later
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
