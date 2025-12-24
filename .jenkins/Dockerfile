FROM jenkins/jenkins:latest

# might be useful later when creating builds within jenkins
# https://www.jenkins.io/doc/book/installing/docker/#downloading-and-running-jenkins-in-docker

COPY ./jenkins.yml /var/jenkins_home/jenkins.yaml

COPY ./plugins.txt /usr/share/jenkins/ref/plugins.txt

RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt