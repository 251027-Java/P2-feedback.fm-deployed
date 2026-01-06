FROM jenkins/jenkins:latest

# https://www.jenkins.io/doc/book/installing/docker/#downloading-and-running-jenkins-in-docker

USER root

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

# https://kubernetes.io/docs/tasks/tools/install-kubectl-linux/#install-using-native-package-management
RUN apt-get install -y apt-transport-https ca-certificates curl gnupg
RUN curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.35/deb/Release.key | gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
RUN chmod 644 /etc/apt/keyrings/kubernetes-apt-keyring.gpg
RUN echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.35/deb/ /' | tee /etc/apt/sources.list.d/kubernetes.list
RUN chmod 644 /etc/apt/sources.list.d/kubernetes.list
RUN apt-get update
RUN apt-get install -y kubectl

# https://helm.sh/docs/intro/install/#from-apt-debianubuntu
RUN apt-get install -y curl gpg apt-transport-https
RUN curl -fsSL https://packages.buildkite.com/helm-linux/helm-debian/gpgkey | gpg --dearmor | tee /usr/share/keyrings/helm.gpg > /dev/null
RUN echo "deb [signed-by=/usr/share/keyrings/helm.gpg] https://packages.buildkite.com/helm-linux/helm-debian/any/ any main" | tee /etc/apt/sources.list.d/helm-stable-debian.list
RUN apt-get update
RUN apt-get install -y helm

RUN apt-get clean && rm -rf /var/lib/apt/lists/*

USER jenkins

COPY ./configs/ /usr/share/jenkins/ref/casc_configs/
COPY ./plugins.txt /usr/share/jenkins/ref/plugins.txt

RUN jenkins-plugin-cli -f /usr/share/jenkins/ref/plugins.txt