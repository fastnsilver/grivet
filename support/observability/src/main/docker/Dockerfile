FROM bellsoft/liberica-openjre-alpine:21
MAINTAINER Chris Phillipson <fastnsilver@gmail.com>
RUN mkdir -p /opt/@project.artifactId@/bin
COPY @project.artifactId@-@project.version@-exec.jar /opt/@project.artifactId@/bin
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /opt/@project.artifactId@/bin/@project.artifactId@-@project.version@-exec.jar