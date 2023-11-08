FROM bellsoft/liberica-openjdk-alpine:21

LABEL author="Chris Phillipson"
LABEL email="fastnsilver@gmail.com"

ENV MAVEN_VERSION="3.9.5" \
    M2_HOME=/usr/lib/mvn \
    MAVEN_OPTS="-Xms64m -Xmx512m -XX:+UseG1GC -XX:+UseStringDeduplication"

RUN apk add --update wget && \
  cd /tmp && \
  wget "http://ftp.unicamp.br/pub/apache/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz" && \
  tar -zxvf "apache-maven-$MAVEN_VERSION-bin.tar.gz" && \
  mv "apache-maven-$MAVEN_VERSION" "$M2_HOME" && \
  ln -s "$M2_HOME/bin/mvn" /usr/bin/mvn && \
  apk del wget && \
  rm /tmp/* /var/cache/apk/*