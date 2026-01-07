/*
possibly useful references in the future
https://www.jenkins.io/blog/2020/04/16/github-app-authentication/#how-do-i-get-an-api-token-in-my-pipeline
https://plugins.jenkins.io/checks-api/
 */

// NOTE: cannot use pipeline steps, such as 'sh', nested two or more levels down in methods.
// this is why you see me calling stuff like catchError() even though I have a function
// called markStageFailure()

def fbfm = [
    run: [
        force: false,
        skip: false,
    ],
    isDefault: false,
    isPrToDefault: false,
    canBuild: false,
    changes: [:],
    test: [:],
    build: [:],
    allSuccessful: true,
    microservices: [
        album: [name: 'album-service', directory: 'backend/album-service', test: true],
        artist: [name: 'artist-service', directory: 'backend/artist-service', test: true],
        eureka: [name: 'eureka-server', directory: 'backend/eureka-server', test: false],
        gateway: [name: 'gateway', directory: 'backend/gateway', test: false],
        history: [name: 'history-service', directory: 'backend/history-service', test: true],
        playlist: [name: 'playlist-service', directory: 'backend/playlist-service', test: true],
        song: [name: 'song-service', directory: 'backend/song-service', test: true],
        spotify: [name: 'spotify-integration-service', directory: 'backend/spotify-integration-service', test: false],
        logging: [name: 'logging-service', directory: 'backend/logging-service', test: false],
        listener: [name: 'listener-service', directory: 'backend/listener-service', test: true],
    ]
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

def checkForChanges = { ref ->
    def cmd = ".jenkins/scripts/changes-count.sh ${ref}"
    fbfm.changes['frontend'] = sh(returnStdout: true, script: "${cmd} '^frontend'").trim() != '0'
    fbfm.changes['jenkinsfile'] = sh(returnStdout: true, script: "${cmd} '^Jenkinsfile'").trim() != '0'

    fbfm.microservices.values().each { service ->
        fbfm.changes[service.name] = sh(returnStdout: true, script: "${cmd} '^${service.directory}'").trim() != '0'
    }
}

def determineReference = { ->
    // https://javadoc.jenkins-ci.org/hudson/scm/ChangeLogSet.html
    def prCreated = currentBuild.changeSets.size() == 0

    // should take care of the following issues
    // https://github.com/251027-Java/P2-feedback.fm-deployed/issues/68
    // https://github.com/251027-Java/P2-feedback.fm-deployed/issues/65
    // check changes relative to the default branch
    if (fbfm.isPrToDefault && (prCreated || currentBuild.previousBuild?.result == 'FAILURE')) {
        sh script: "git fetch origin ${env.GITHUB_DEFAULT_BRANCH}"
        return 'FETCH_HEAD'
    }

    if (!prCreated) {
        // assuming this is git and we only ever have one scm
        def changeSet = currentBuild.changeSets.first()
        echo "${changeSet.kind}: commits: ${changeSet.items.size()}"

        // check changes relative to the last `N` commits since a push can have multiple commits
        return "HEAD~${changeSet.items.size()}"
    }

    // check relative to current commit
    return 'HEAD~1'
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

            def validImageKeys = [ 'frontend' ] as Set
            validImageKeys += fbfm.microservices.values().collect { it -> it.name }

            // build images based on "image-*"
            attributes.findAll { it.startsWith('image-') }.each { attr ->
                def key = attr.substring('image-'.length())
                
                if (validImageKeys.contains(key)) {
                    // indicate successful build to trick system
                    echo "[${key}]: will build"
                    fbfm.build[key] = true
                }
            }

            if (attributes.contains('imageall')) {
                echo '[imageall]: building all images'

                validImageKeys.each { name -> 
                    echo "[${name}]: will build"
                    fbfm.build[name] = true
                }
            }

            validImageKeys.each { name -> 
                fbfm.canBuild |= fbfm.build.containsKey(name)
            }
        }
    }
}

def markStageFailure = { ->
    catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
        sh 'exit 1'
    }
    fbfm.allSuccessful = false
}

def fbfmBuildImage = { args ->
    def directory = args.directory
    def tagSeries = args.tagSeries
    def dockerRepo = args.dockerRepo
    def pushLatest = args.pushLatest
    def branch = (env.CHANGE_BRANCH ?: env.GIT_BRANCH).replaceAll('/', '-')

    def tagName = "${tagSeries}-${branch}-${shortSha()}"
    def chName = "docker hub / ${tagSeries}"

    sh '.jenkins/scripts/docker-prep.sh'
    publishChecks name: chName, title: 'Pending', status: 'IN_PROGRESS'

    dir(directory) {
        try {
            def image = docker.build(dockerRepo)

            docker.withRegistry('', 'docker-hub-cred') {
                image.push(tagName)

                if (pushLatest) {
                    image.push("${tagSeries}-latest")
                }
            }

            publishChecks name: chName, conclusion: 'SUCCESS', title: 'Success'
        } catch (err) {
            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                sh 'exit 1'
            }
            fbfm.allSuccessful = false
            echo "${err}"
            publishChecks name: chName, conclusion: 'FAILURE', title: 'Failed'
        }
    }

    // docker clean up
    sh ".jenkins/scripts/docker-cleanup.sh ${tagName}"
}

def fbfmBuildMicroservice = { args ->
    def directory = args.directory
    def name = args.name

    def chName = "build / ${name}".toString()

    publishChecks name: chName, title: 'Pending', status: 'IN_PROGRESS'

    dir(directory) {
        try {
            sh 'mvn -B package -DskipTests'
            publishChecks name: chName, conclusion: 'SUCCESS', title: 'Success'
            fbfm.build[name] = true
        } catch (err) {
            catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                sh 'exit 1'
            }
            fbfm.allSuccessful = false
            echo "${err}"
            publishChecks name: chName, conclusion: 'FAILURE', title: 'Failed'
        }
    }
}

def fbfmTestMicroservice = { args ->
    def directory = args.directory
    def name = args.name

    def chName = "test / ${name}".toString()

    withChecks(name: chName) {
        dir(directory) {
            try {
                sh 'mvn -B test'
                junit '**/target/surefire-reports/TEST-*.xml'
                fbfm.test[name] = true
            } catch (err) {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    sh 'exit 1'
                }
                fbfm.allSuccessful = false
                echo "${err}"
                publishChecks name: chName, conclusion: 'FAILURE', title: 'Failed'
            }
        }
    }
}

pipeline {
    agent any

    tools {
        jdk 'java25'
        maven 'maven3'
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

                    fbfm.isDefault = env.BRANCH_IS_PRIMARY == 'true'
                    fbfm.isPrToDefault = env.CHANGE_TARGET == env.GITHUB_DEFAULT_BRANCH
                    fbfm.canBuild = fbfm.isDefault

                    def ref = determineReference()
                    checkForChanges(ref)
                    handleCommitAttributes()

                    echo "${groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(fbfm))}"
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
                dir('frontend') {
                    script {
                        def chName = 'lint / frontend'
                        def res = null

                        publishChecks name: chName, title: 'Pending', status: 'IN_PROGRESS'

                        try {
                            sh 'biome ci --colors=off --reporter=summary > frontend-code-quality.txt'
                            res = [con: 'SUCCESS', title: 'Success']
                        } catch (err) {
                            markStageFailure()
                            echo "${err}"
                            res = [con: 'FAILURE', title: 'Failed']
                        }

                        def output = readFile file: 'frontend-code-quality.txt'
                        echo output

                        publishChecks name: chName, conclusion: res.con, title: res.title,
                            summary: limitText(output)
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
                script {
                    def chName = 'build / frontend'
                    publishChecks name: chName, title: 'Pending', status: 'IN_PROGRESS'

                    dir('frontend') {
                        try {
                            sh 'npm ci && npm run build'
                            publishChecks name: chName, conclusion: 'SUCCESS', title: 'Success'
                            fbfm.build.frontend = true
                        } catch (err) {
                            markStageFailure()
                            echo "${err}"
                            publishChecks name: chName, conclusion: 'FAILURE', title: 'Failed'
                        }
                    }
                }
            }

            post {
                always {
                    // need to clean the workspace here because it's a separate workspace from the workspace that gets
                    // cleaned at the end of this file. this installs node_modules, so it can take up a large amount of
                    // disk
                    cleanWs()
                }
            }
        }

        stage('image frontend') {
            when {
                expression { fbfm.build.frontend && fbfm.canBuild }
            }

            steps {
                script {
                    fbfmBuildImage(name: 'frontend', directory: 'frontend', tagSeries: 'fe',
                        dockerRepo: 'minidomo/feedbackfm', pushLatest: fbfm.isDefault
                    )
                }
            }
        }

        stage('test build image microservices') {
            steps {
                script {
                    fbfm.microservices.values().each { service ->
                        def shouldRun = !fbfm.run.skip &&
                            (
                                fbfm.run.force ||
                                (
                                    (fbfm.isPrToDefault || fbfm.isDefault) &&
                                    (fbfm.changes.jenkinsfile || fbfm.changes[service.name])
                                )
                            )

                        if (shouldRun) {
                            if (service.test) {
                                stage("test ${service.name}") {
                                    fbfmTestMicroservice(name: service.name, directory: service.directory)
                                }
                            }

                            stage("build ${service.name}") {
                                fbfmBuildMicroservice(name: service.name, directory: service.directory)
                            }
                        }

                        if (fbfm.build[service.name] && fbfm.canBuild) {
                            stage("image ${service.name}") {
                                fbfmBuildImage(directory: service.directory, tagSeries: "be-${service.name}",
                                    dockerRepo: 'minidomo/feedbackfm', pushLatest: fbfm.isDefault
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            sh '.jenkins/scripts/docker-cleanup.sh'
            // delete the workspace after to prevent large disk usage
            cleanWs()

            script {
                if (!fbfm.allSuccessful) {
                    error 'Not all stages were successful'
                }
            }
        }
    }
}
