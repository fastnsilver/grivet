# Developer Notes

This is a [Spring Boot](http://projects.spring.io/spring-boot/) application.  It is initialized with:

[AppInit.java](https://github.com/fastnsilver/grivet/blob/master/core/grivet/src/main/java/com/fns/grivet/AppInit.java)


## Prerequisites

* [Docker Toolbox](http://docs.docker.com/mac/started/); `docker`, `docker-machine` and `docker-compose` are required
* Java [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 1.8.0_45 or better
* [Maven](https://maven.apache.org/download.cgi) 3.3.3 or better
* an RDBMS (H2 is the default w/ no additional configuration); see [application.yml](https://github.com/fastnsilver/grivet/blob/master/core/grivet/src/main/resources/application.yml) for details


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

Assuming you have installed Docker Machine, Docker Compose and Docker
If not, it's highly recommended to install each via [Homebrew](http://brew.sh/) with

```
brew install docker-machine
brew install docker-compose
brew install docker
```

#### Build images

```
mvn clean install
```

#### Pull images

Visit [Dockerhub](https://hub.docker.com/u/fastnsilver/)

Pull all the grivet images


#### Run images

```
cd docker
docker-compose up -d
```

##### Running a local development environment

@see https://forums.docker.com/t/using-localhost-for-to-access-running-container/3148

On a Mac we cannot access running Docker containers from localhost.

After running `docker-machine ip {env}` where `{env}` is your instance of a docker-machine, add an entry in `/etc/hosts` that maps `DOCKER_HOST` IP address to a memorable hostname.


Caveats: 

* Docker image currently bootstraps against a MySQL back-end


#### Work with images

This section to be written


#### Stop images (and remove them)

```
docker-compose stop
docker-compose rm -f
```
