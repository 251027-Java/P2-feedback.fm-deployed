FROM jenkins/jenkins:latest

# https://www.jenkins.io/doc/book/installing/docker/#downloading-and-running-jenkins-in-docker

USER root

# install docker cli
# https://docs.docker.com/engine/install/debian/#install-using-the-repository
RUN apt-get update
RUN apt-get install -y ca-certificates curl
RUN install -m 0755 -d /etc/apt/keyrings
RUN curl -fsSL https://download.docker.com/linux/debian/gpg -o /etc/apt/keyrings/docker.asc
RUN chmod a+r /etc/apt/keyrings/docker.asc

RUN tee /etc/apt/sources.list.d/docker.sources <<EOF
Types: deb
URIs: https://download.docker.com/linux/debian
Suites: $(. /etc/os-release && echo "$VERSION_CODENAME")
Components: stable
Signed-By: /etc/apt/keyrings/docker.asc
EOF
RUN apt-get update

RUN apt-get install -y docker-ce-cli
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

USER jenkins

COPY ./jenkins.yml /var/jenkins_home/jenkins.yaml
COPY ./plugins.txt /usr/share/jenkins/ref/plugins.txt

RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt