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

where `<profile-name>` should be replaced with either `h2,insecure` or `mysql,insecure`

> If you activated the `mysql` profile you should already have provisioned a MySQL instance.

> E.g., on a Mac, you could install [Homebrew](http://brew.sh/), then install MySQL with

>```
> brew install mysql
>```

> Then start the instance with `mysql.server start`

> The instance will need to have a schema created before attempting to run the service as directed above.

> Consult [application.yml](https://github.com/fastnsilver/config-repo/blob/master/application.yml) in order to override `spring.datasource.*` properties.



### with Docker

#### via Docker Desktop

Install [Docker Desktop](https://www.docker.com/products/docker-desktop/), then skip to [Onward with Docker](#onward-with-docker).

#### via Multipass

 [Multipass](https://multipass.run/)

On a Mac

* Install [Homebrew](http://brew.sh/)

```
brew install multipass
```

The commands below a) provisions a Docker host named `dev` with 2 CPU, 20Gb RAM, and 40Gb disk space and b) drops you into a shell on that host

```
multipass launch docker -c 2 -m 20G -d 40G -n dev
multipass shell dev
```

You could also execute the following script which will perform the step above on your behalf

```
./provision.sh {1}
```

where `{1}` above would be replaced with whatever you want to name your Multipass instance

Caveat: You should have at least 20GB of memory and 40GB of disk space on your laptop or workstation.

You'll need to install some additional tools to make the VM useful.

```
sudo apt install unzip zip
curl -s "https://get.sdkman.io" | bash
sdk install java 17.0.9-librca
sdk install maven 3.9.5
```

You will also want to fetch the source for this repo too

```
git clone https://github.com/pacphi/grivet
```

To exit the shell at any time, type `exit` and press the `Return` key.


To destroy your docker machine, you could execute

```
./destroy.sh {1}
```

where `{1}` above would be replaced with the name of an existing Multipass instance

Caution! This will remove the VM hosting all your Docker images.


#### Onward with Docker

##### Prereqs

* Obtain Github personal access token
  * from the command line (not inside Multipass instance), execute

  ```
  gh auth login
  gh auth token
  ```

  * copy-and-paste the token as the value for [SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD](../docker/docker-compose.yml#L136)
* Prepare a private Git repository

  ```
  # TODO add steps to create repo, unpack .zip, git add, git commit, and git push
  ```

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

If you haven't yet built images locally, you can visit [Dockerhub](https://hub.docker.com/u/fastnsilver/) to pull pre-built `fastnsilver/grivet-*` images


##### Run images

```
./startup.sh {1}
```

where `{1}` above would be replaced with either `standalone` or `pipeline`


#### Work with images

Services are accessible via the Docker host (or IP address) and port

Service            |  Port
-------------------|-------
Edge Service (Spring Cloud Gateway)| 9999
Config Server      | 8888
Discovery (Eureka) | 8761
Prometheus         | 9090
Grafana            | 3000
Grivet Standalone  | 8080
PHP MySQL Admin    | 4000
MySQL              | 3306
Elasticsearch      | 9200
Logstash           | 5000
Kibana             | 5601
CAdvisor           | 9080

If making requests via Edge Service, consult `zuul.routes` in [application.yml](https://github.com/fastnsilver/grivet/blob/master/support/api-gateway/src/main/resources/application.yml).  Prepend route to each service's public API.


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