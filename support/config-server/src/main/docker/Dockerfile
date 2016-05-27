FROM anapsix/alpine-java:8
MAINTAINER Chris Phillipson <fastnsilver@gmail.com>
VOLUME /tmp
ADD @project.artifactId@-@project.version@.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]