FROM alpine:3 AS build-env
ENV OTEL_VERSION=1.31.0
WORKDIR /app
RUN apk --no-cache add curl
RUN curl -LO https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_VERSION}/opentelemetry-javaagent.jar

FROM bellsoft/liberica-openjre-alpine:21
LABEL author="Chris Phillipson"
LABEL email="fastnsilver@gmail.com"
ENV JAVA_TOOL_OPTIONS "-javaagent:./opt/@project.artifactId@/bin/opentelemetry-javaagent.jar"
RUN mkdir -p /opt/@project.artifactId@/bin
COPY --from=build-env /app/opentelemetry-javaagent.jar /opt/@project.artifactId@/bin/opentelemetry-javaagent.jar
COPY @project.artifactId@-@project.version@.jar /opt/@project.artifactId@/bin
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom $JAVA_TOOL_OPTIONS -jar /opt/@project.artifactId@/bin/@project.artifactId@-@project.version@.jar