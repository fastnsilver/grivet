# Roadmap

This project will remain humble in its design and has no aspirations to match the features or scaling characteristics of other projects like [Hadoop](https://hadoop.apache.org/) + Apache [Orc](https://orc.apache.org/), [ElasticSearch](https://www.elastic.co/products/elasticsearch), [Scylla DB](http://www.scylladb.com/) or [SenseiDB](http://www.senseidb.com/).


## 0.0.1 

- [x] Basic implementation supporting registration, storage and retrieval of `text`, `numbers`, and `dates`
- [x] Single and batch POST capable `/register` and `/store` end-points
- [x] Dynamic query support
- [x] Named query support via /query end-point; both `SELECT` queries and `CALL`s to stored procedures
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

- [ ] Improve test coverage
- [ ] Begin tracking technical debt with [Sonarqube](https://hub.docker.com/_/sonarqube/)
- [ ] Run [Gatling](http://gatling.io/#/) tests and publish performance metrics for a) write-intensive and b) read-intensive application

## 0.3.0

- [ ] Password-protect service administration and discovery
- [ ] Secure endpoints via Stormpath [integration](https://stormpath.com/blog/build-spring-boot-spring-security-app/)
- [ ] Enable [TLS/HTTPS](http://security.stackexchange.com/questions/5126/whats-the-difference-between-ssl-tls-and-https) 
- [x] Define roles to limit access to administrators for registering new types and queries
- [ ] All writes are audited (User is associated with record)

## 0.4.0

- [ ] Implement [Vaadin](https://vaadin.com/home)-based administrative UI

## 0.5.0

- [ ] Add cache provider (e.g., Redis)
- [ ] Plumb Hystrix/Turbine for latency and fault protection

## 0.6.0

- [ ] Gradle build