# Developer Notes

This is a [Spring Boot](http://projects.spring.io/spring-boot/) application.  It is initialized with:

[App.java](https://github.com/fastnsilver/grivet/blob/master/src/main/java/com/fns/grivet/App.java)


## Prerequisites

* [Docker Toolbox](http://docs.docker.com/mac/started/); `docker`, `docker-machine` and `docker-compose` are required
* Java [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 1.8.0_45+
* [Maven](https://maven.apache.org/download.cgi) 3.3.3
* an RDBMS (H2 is the default w/ no additional configuration); see [application.yml](https://github.com/fastnsilver/grivet/blob/master/src/main/resources/application.yml) for details


## How to build

```
$ mvn clean install
```


## How to run

### with Spring Boot

```
$ mvn spring-boot:run
```

Or

```
$ mvn spring-boot:run -Dspring.profiles.active=<profile-name>
```

where `<profile-name>` could be replaced with `h2` or `mysql`

Or 

```
$ java -jar grivet-x.x.x.jar
```

where `x.x.x` is a version like `0.0.1-SNAPSHOT`

Or

```
$ java -jar grivet-x.x.x.jar -Dspring.profiles.active=<profile-name>
```

likewise replacing `<profile-name>`


### with Docker

Assuming you have installed Docker...


#### Build image

```
mvn package
```

#### Pull image

```
docker pull fastnsilver/grivet:latest
```

#### Run image

```
mvn docker:start
```

Or

```
docker run -i -t -p 8080:8080 fastnsilver/grivet:latest /bin/bash
```

Visit `192.168.59.103:8080` in a browser

Caveats: 

* Docker image currently bootstraps against an H2 back-end
* [Bug] Observed to take up to 6 minutes to start the app on Mac OS 10.10.3 running VirtualBox 4.3.30 r101610 and boot2docker 1.7.1


#### Stop image (and remove)

```
mvn docker:stop
```

Or

```
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
```
