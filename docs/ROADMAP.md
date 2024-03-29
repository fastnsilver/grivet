# Roadmap

This project will remain humble in its design and has no aspirations to match the features or scaling characteristics of other more mature projects like [Hadoop](https://hadoop.apache.org/) + Apache [Orc](https://orc.apache.org/), [ElasticSearch](https://www.elastic.co/products/elasticsearch), [Scylla DB](http://www.scylladb.com/) or [SenseiDB](http://www.senseidb.com/).


## 0.0.1

- [x] Basic implementation supporting registration, storage and retrieval of `text`, `numbers`, and `dates`
- [x] Single and batch POST capable `/type/register` and `/type/store` end-points
- [x] Dynamic query support
- [x] Named query support via `/query` end-point; both `SELECT` queries and `CALL`s to stored procedures
- [x] Link [JSON Schema](http://spacetelescope.github.io/understanding-json-schema/) with a registered type; on subsequent store requests for type, type will be validated against schema before attempt to persist
- [x] Documentation authored and published inc. [API](http://fastnsilver.github.io/grivet/grivet/rest-api.html), [Javadoc](http://fastnsilver.github.io/grivet/apidocs/index.html), Maven Site to Github [Pages](http://fastnsilver.github.io/grivet/)
- [x] Continuous integration builds configured on [Shippable](http://docs.shippable.com/)
- [x] [Docker](https://www.docker.com/) container (app w/ [H2](http://www.h2database.com/html/main.html) back-end)
- [x] Publish Docker [image](https://hub.docker.com/r/fastnsilver/grivet/) to DockerHub

## 0.1.0

- [x] Switch to [Travis-CI](https://travis-ci.org/) plus [Coveralls](https://coveralls.io) for continuous integration and code coverage reports respectively
- [x] Re-org project structure; introduce [Spring Cloud](http://projects.spring.io/spring-cloud/) and [Netflix OSS](http://cloud.spring.io/spring-cloud-netflix/spring-cloud-netflix.html) to provide cloud-native infrastructure
- [x] Docker [Compose](https://docs.docker.com/compose/); launch variant Docker image(s) sharing a single data-store (e.g., [MySQL](https://www.mysql.com/))
- [x] Add Elasticsearch, Logstash, Kibana, and Logspout for log management
- [x] Spring Boot [Admin](https://github.com/codecentric/spring-boot-admin#spring-boot-admin) available in addition to Eureka
- [x] Upgrade to [HikariCP](http://brettwooldridge.github.io/HikariCP/) for connection pooling
- [x] Fix date/time handling flaw
- [x] Add Grafana, Graphite, statsd for metrics

## 0.2.0

- [x] Prefix all end-points with `/type` except for `/query`
- [x] Improve test coverage
- [x] Switch to [CodeCov](https://codecov.io/) for coverage reporting
- [x] Begin tracking technical debt with [Sonarqube](https://hub.docker.com/_/sonarqube/)
- [x] Secure endpoints via Stormpath [integration](https://stormpath.com/blog/build-spring-boot-spring-security-app/)
- [x] Enable [TLS/HTTPS](http://security.stackexchange.com/questions/5126/whats-the-difference-between-ssl-tls-and-https)
- [x] Define roles to limit access to administrators for registering new types and queries
- [x] All writes are audited (User is associated with record)

## 0.3.0

- [x] Re-org modules to reduce coupling
- [x] All-in w/ [Lombok](https://projectlombok.org/)
- [x] Upgrade to Hibernate 5.2.x
- [x] Leverage Spring Data's [AuditingEntityListener](http://docs.spring.io/spring-data/data-jpa/docs/1.7.0.DATAJPA-580-SNAPSHOT/reference/html/auditing.html) for entity auditing
- [x] Endpoints now start with `/api/v1`; and another re-definition of resource names
- [x] Switch UAA provider from [Stormpath](https://stormpath.com/blog/stormpaths-new-path) to [Auth0](https://manage.auth0.com/#/)
- [x] Switch to [Codeship](https://app.codeship.com/projects/201927/) for builds and scan for vulnerabilities with [Snyk](https://snyk.io/org/fastnsilver/projects?origin=github)
- [x] Introduce reverse [proxy](https://github.com/fastnsilver/grivet/issues/9), adjust endpoints behind this gateway
- [x] Add registration, storage and retrieval support for `boolean` entity attribute values
- [x] Allow for querying, updating, and deleting an entity by its object identifier
- [x] [Upgrade](https://github.com/fastnsilver/grivet/issues/8) Docker Compose configuration
- [x] Switch documentation generation from Springfox Swagger to Spring RestDocs
- [x] Apache Kafka [integration](https://github.com/fastnsilver/grivet/issues/7)

## 0.4.0

- [x] Replace DropWizard with [Micrometer](http://micrometer.io)
- [x] Upgrade implementation to Spring Framework 5, Spring Boot 2.0 and Spring Cloud Finchley
- [x] All tests reimplemented using [JUnit 5](http://junit.org/junit5/docs/current/user-guide/) API

## 1.0.0

- [x] Major upgrade to Spring Boot 3.1.5
  - back from a long hiatus in development
- [x] Java 17 baseline
- [x] API Gateway provider swap (now Spring Cloud Gateway)
- [x] Dead Auth0 swapped for Okta Spring Boot 3.x API
- [x] Flyway adjustments (preparing for 10.0.0)
- [x] Docker Compose infrastructure updated
- [x] Resurrection of Spring Boot Admin
- [x] Add tracing (via Open-Telemetry APIs) and integrate [Signoz](https://signoz.io/)
- [x] Continuous integration builds now Github Action based
- [x] Updated Maven Site published
- [x] Updated Docker container images published to Dockerhub
- Implement deployment option
  - [x] Tanzu Application Service

## 1.1.0

- [x] Upgrade to Spring Boot 3.2.0
- [x] Java 21 baseline
- [x] [Delombok](https://github.com/fastnsilver/grivet/issues/151) source code
  - Remove annotations
  - Remove back-flipping in Maven POMs
- [ ] Endpoint security policy implemented with Spring Cloud Gateway and Spring Security 6
- [ ] Password-protect service administration and discovery

## 1.2.0

- [ ] Switch out usage of JdbcTemplate for JdbcClient
- [ ] Add cache provider (e.g., Redis)

## 1.3.0

- [ ] Implement [Vaadin](https://vaadin.com/home)-based administrative UI

## 1.4.0

- [ ] Run [Gatling](http://gatling.io/#/) tests and publish performance metrics for a) write-intensive and b) read-intensive application
- Implement deployment options
  - [ ] Kubernetes (e.g., AKS, EKS, GKE)
  - [ ] Azure Spring Apps Enterprise
  - [ ] Tanzu Application Platform
