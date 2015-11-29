FROM relateiq/oracle-java8
MAINTAINER Chris Phillipson <fastnsilver@gmail.com>
VOLUME /tmp
ADD @project.artifactId@-@project.version@.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","-Djava.security.egd=file:/dev/./urandom","/app.jar"]