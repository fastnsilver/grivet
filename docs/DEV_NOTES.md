# Developer Notes

This is a [Spring Boot](http://projects.spring.io/spring-boot/) application.  

## Prerequisites

* `docker` and `compose` plugin are required, you have options
  * See [Docker for Mac](https://docs.docker.com/docker-for-mac/)
  * See [Docker for Windows 10](https://docs.docker.com/docker-for-windows/)
* Java [JDK](https://www.oracle.com/java/technologies/downloads/#java17) 17.0.9 or better
* [Maven](https://maven.apache.org/download.cgi) 3.9.5 or better
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
  cd /tmp
  gh repo create grivet-repo-config --private --clone
  cd grivet-repo-config
  curl -LO https://raw.githubusercontent.com/fastnsilver/grivet/main/config-repo/template.zip
  unzip template.zip
  rm template.zip
  git add --all
  git commit -m "Initial set of application configuration"
  git push origin main
  ```

##### Build images

```
./build.sh
```


##### Publish images

Assumes proper authentication credentials have been added to `$HOME/.m2/settings.xml`.

To create an access token in [Dockerhub](https://hub.docker.com), authenticate your account, then visit https://hub.docker.com/settings/security.

Then consult:

* [Authenticating with Private Registries](https://dmp.fabric8.io/#authentication)

Visit [core/deployables](../core/deployables/) and [support](../support/) subdirectories one-by-one and execute

```
mvn docker:push
```


##### Pull images

If you haven't yet built images locally, you can visit [Dockerhub](https://hub.docker.com/u/fastnsilver/) to pull pre-built `fastnsilver/grivet-*` images


##### Run images

```
./startup.sh {1}
```

where `{1}` above would be replaced with `standalone`, `pipeline-kafka`, or `pipeline-rabbit`


#### Work with images

Services are accessible via the Docker host (or IP address) and port

Service            |  Port
-------------------|-------
Edge Service (Spring Cloud Gateway)| 80
Config Server      | 8888
Discovery (Eureka) | 8761
Prometheus         | 9090
Grafana            | 3000
Grivet Standalone  | 8080
^ Grivet Ingest      | 9081
^ Grivet Admin       | 9085
^ Grivet Persistence | 9082
^ Grivet Query       | 9083
PHP MySQL Admin    | 5050
Spring Boot Admin  | 5555
MySQL              | 3306
Kafka              | 29092
Kafka UI           | 8090
Rabbit Dashboard   | 15672
Zookeeper          | 22181
Signoz             | 3301
CAdvisor           | 9080

If making requests via Edge Service, consult `spring.cloud.gateway.routes` in the `grivet-api-gateway.yml` file within your own [grivet-config-repo](#prereqs).


#### Stop images (and remove them)

```
./shutdown.sh {1}
```

where `{1}` above would be replaced with `standalone`, `pipeline-kafka`, or `pipeline-rabbit`


## Working with Maven Site

### Stage

Make sure your `$HOME/.m2/settings.xml` file has an entry [like this](https://stackoverflow.com/questions/67001968/how-to-disable-maven-blocking-external-http-repositories):

```
<mirrors>
  ...
  <mirror>
    <id>maven-default-http-blocker</id>
    <mirrorOf>external:http:*</mirrorOf>
    <name>Pseudo repository to mirror external repositories initially using HTTP.</name>
    <url>http://0.0.0.0/</url>
    <blocked>false</blocked>
  </mirror>
</mirrors>
```

then

```
mvn site site:stage -Pdocumentation
```

### Publish

You must setup a `gh-pages` (orphan) branch in advance.  In addition, appropriate authentication credentials need to be declared in `$HOME/.m2/settings.xml`

E.g.,

```
<servers>
  ...
  <server>
    <id>github</id>
    <username>git</username>
    <privateKey>/home/cphillipson/.ssh/fastnsilver_github_ed25519</privateKey>
  </server>
</servers>
```

See:

* [Generating a new SSH key and adding it to the ssh-agent](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)
* [Adding a new SSH key to your GitHub account](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account?tool=cli)
* [Creating Project Pages manually](https://help.github.com/articles/creating-project-pages-manually/)
* [Security and Deployment Settings](http://maven.apache.org/guides/mini/guide-deployment-security-settings.html)


Then

```
mvn scm-publish:publish-scm -Pdocumentation
```

### Review

* [Maven Site](http://fastnsilver.github.io/grivet/)