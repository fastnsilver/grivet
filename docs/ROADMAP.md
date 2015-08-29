# Roadmap

This project will remain humble in its design and has no aspirations to match the features or scaling characteristics of other projects like Hadoop / Apache Orc, ElasticSearch or SenseiDB.


## 0.0.1 

- [x] Basic implementation supporting registration, storage and retrieval of text, numbers, and dates
- [x] Single and batch POST capable /register and /store end-points
- [x] Dynamic query support
- [x] Named query support via /query end-point; both SELECT queries and CALLs to stored procedures
- [x] Link JSON Schema with a registered type; on subsequent store requests for type, type will be validated against schema before attempt to persist
- [x] Documentation authored and published inc. API, [Javadoc](http://fastnsilver.github.io/grivet/apidocs/index.html), Maven Site to Github [Pages](http://fastnsilver.github.io/grivet/)
- [x] Continuous integration builds configured on [Shippable](http://docs.shippable.com/)
- [x] Docker container (app w/ H2 back-end)
- [x] Publish Docker [image](https://hub.docker.com/r/fastnsilver/grivet/) to DockerHub

## 0.1.0

- [ ] Re-org project structure; introduce Spring Cloud and Netflix OSS for cloud-native infrastructure
- [ ] Docker Compose; launch variant Docker image(s) sharing a single data-store (e.g., MySQL)
- [x] Spring Boot [Admin](https://github.com/codecentric/spring-boot-admin#spring-boot-admin) available
- [x] Upgrade to HikariCP for connection pooling
- [ ] Introduce OAuth2/SSO security
- [ ] Enable HTTPS 
- [x] Define roles to limit access to administrators for registering new types and queries
- [ ] All writes are audited (user is associated with record)
- [ ] Improve test coverage
- [ ] Run Gatling tests and publish performance metrics for a) write-intensive and b) read-intensive application

## 0.2.0

- [ ] Implement Vaadin-based administrative UI
