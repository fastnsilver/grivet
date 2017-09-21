# Developer Notes

This is a [Spring Boot](http://projects.spring.io/spring-boot/) application.  

## Prerequisites

* `docker` and `docker-compose` are required, you have options
  * See [Docker Toolbox](https://www.docker.com/products/docker-toolbox) -- note that this is considered a legacy option
  * See [Docker for Mac](https://docs.docker.com/docker-for-mac/)
  * See [Docker for Windows 10](https://docs.docker.com/docker-for-windows/)
* Java [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 1.8u144 or better
* [Maven](https://maven.apache.org/download.cgi) 3.5.0 or better
* an RDBMS
  * See [application.yml](https://github.com/fastnsilver/config-repo/blob/master/application.yml) for details
  * Enable the `h2` profile for in-memory database
  * Enable the `mysql` profile for MySQL database (assumes MySQL instance was provisioned)


## How to build

> If you have installed `Docker for Mac` or `Docker for Windows` installed, create a file in project root named `.local`

```
$ mvn clean install
```


## How to run

### with Spring Boot and Spring Cloud Services

> Follow these steps to run the `grivet-standalone` service

Open 3 terminal shells:

#### 1

```
$ cd support/config-server
$ mvn spring-boot:run
```

#### 2

```
$ cd support/discovery-service
$ mvn spring-boot:run
```

#### 3

```
$ cd core/deployables/grivet-standalone
$ mvn spring-boot:run -Dspring.profiles.active=<profile-name>
```

where `<profile-name>` should be replaced with either `h2` or `mysql`

> If you activated the `mysql` profile you should already have provisioned a MySQL instance.

> E.g., on a Mac, you could install [Homebrew](http://brew.sh/), then install MySQL with

>```
>brew install mysql
>```

> Then start the instance with `mysql.server start`

> The instance will need to have a schema created before attempting to run the service as directed above.

> Consult [application.yml](https://github.com/fastnsilver/config-repo/blob/master/application.yml) in order to override `spring.datasource.*` properties.



### with Docker

#### (Optional) Docker Toolbox pre-requisites
Assuming you have installed VirtualBox, Docker Machine, Docker Compose and Docker.

If not, it's highly recommended (on a Mac) to install each via [Homebrew](http://brew.sh/) with

```
brew tap caskroom/cask
brew install brew-cask
brew cask install virtualbox

brew install docker-machine
brew install docker-compose
brew install docker
```

The instruction below provisions a Docker host named `dev` with 2 CPU, 10Gb RAM and 40Gb disk space

```
docker-machine create --driver virtualbox --virtualbox-cpu-count "2" --virtualbox-disk-size "40000" --virtualbox-memory "10240" dev
```

You could also execute the following script which will perform the first step above on your behalf

```
./provision.sh {1}
```

where `{1}` above would be replaced with whatever you want to name your docker-machine

Caveat: You should have at least 16GB of memory and 40GB of disk space on your laptop or workstation.


To begin using it

```
eval $(docker-machine env dev)
```


Lastly, to destroy your docker machine, you could execute

```
./destroy.sh {1}
```

where `{1}` above would be replaced with an existing docker-machine name

Caution! This will remove the VM hosting all your Docker images.

#### Onward with Docker

##### Build images

```
./build.sh
```


##### Publish images

Assumes proper authentication credentials have been added to `$HOME/.m2/settings.xml`. See:

* [Authenticating with Private Registries](https://dmp.fabric8.io/#authentication)

```
mvn docker:push
```


##### Pull images

Visit [Dockerhub](https://hub.docker.com/u/fastnsilver/)

Pull all the `fastnsilver/grivet-*` images


##### Run images

```
./startup.sh {1}
```

where `{1}` above would be replaced with either `standalone` or `pipeline`


##### Running a local development environment (with Docker Toolbox)

> The following holds true only if you opted to install Docker Toolbox

@see https://forums.docker.com/t/using-localhost-for-to-access-running-container/3148

On a Mac we cannot access running Docker containers from localhost.

After running `docker-machine ip {env}` where `{env}` is your instance of a docker-machine, add an entry in `/etc/hosts` that maps `DOCKER_HOST` IP address to a memorable hostname.


Caveats: 

* Docker image currently bootstraps against a MySQL back-end


#### Work with images

Services are accessible via the Docker host (or IP address) and port 

Service            |  Port
-------------------|-------
Spring Boot Admin  | 5555
Microservices Dash | 8088
Edge Service (Zuul)| 80
Config Server      | 8888
Discovery (Eureka) | 8761
Graphite           | 8000
Grafana            | 3000
Grivet Standalone  | 8080
PHP MySQL Admin    | 4000
MySQL              | 3306
Elasticsearch      | 9200
Logstash           | 5000
Kibana             | 5601
CAdvisor           | 9080

If making requests via Edge Service, consult `zuul.routes` in [application.yml](https://github.com/fastnsilver/grivet/blob/master/support/api-gateway/src/main/resources/application.yml).  Prepend
route to each service's public API.


#### Stop images (and remove them)

```
./shutdown.sh {1}
```

where `{1}` above would be replaced with either `standalone` or `pipeline`


## Working with Maven Site 

### Stage

```
mvn site site:stage -Pdocumentation
```

### Publish

Assumes a `gh-pages` (orphan) branch has been set up in advance.  In addition, appropriate authentication credentials have been declared in `$HOME/.m2/settings.xml`. See:

* [Creating Project Pages manually](https://help.github.com/articles/creating-project-pages-manually/)
* [Security and Deployment Settings](http://maven.apache.org/guides/mini/guide-deployment-security-settings.html)

```
mvn scm-publish:publish-scm -Pdocumentation
```

### Review

* [Maven Site](http://fastnsilver.github.io/grivet/)