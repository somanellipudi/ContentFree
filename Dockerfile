FROM debian:stretch

ARG MAVEN_VERSION=3.5.4
ARG USER_HOME_DIR="/root"
ARG MAVEN_SHA=ce50b1c91364cb77efe3776f756a6d92b76d9038b0a0782f7d53acf1e997a14d
ARG MAVEN_BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"


RUN apt-get update -y && \
  apt-get install \
    apt-utils \
    dialog \
    gnupg \
    lsb-release \
    curl -y && \ 
  export CLOUD_SDK_REPO="cloud-sdk-$(lsb_release -c -s)" && \
  echo "deb http://packages.cloud.google.com/apt $CLOUD_SDK_REPO main" | tee -a /etc/apt/sources.list.d/google-cloud-sdk.list && \
  curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add - && \
  apt-get update -y && \
  apt-get install google-cloud-sdk -y

# Install all available components
RUN apt-get install google-cloud-sdk \
  google-cloud-sdk \
  google-cloud-sdk-app-engine-go \
  google-cloud-sdk-app-engine-java \
  google-cloud-sdk-app-engine-python \
  google-cloud-sdk-app-engine-python-extras \
  google-cloud-sdk-bigtable-emulator \
  google-cloud-sdk-cbt \
  google-cloud-sdk-datastore-emulator \
  google-cloud-sdk-cloud-build-local \
  google-cloud-sdk-datalab \
  kubectl \
  google-cloud-sdk-pubsub-emulator -y
#
# OpenJDK installation
# https://linuxhint.com/install-openjdk-8-on-debian-9-stretch/
RUN apt-get install openjdk-8-jdk -y

#
# MAVEN installation
# https://github.com/carlossg/docker-maven/blob/f581ea002e5d067deb6213c00a4d217297cad469/jdk-8/Dockerfile
RUN mkdir -p ${MAVEN_HOME} ${MAVEN_HOME}/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && echo "${MAVEN_SHA} /tmp/apache-maven.tar.gz" | sha256sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C ${MAVEN_HOME} --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn

WORKDIR /workspace