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
- [ ] Publish artifact to Maven Central
- [ ] Publish Docker container to DockerHub

## 0.0.2

- [ ] Introduce OAuth2 security
- [ ] Define roles to limit access to administrators for registering new types and queries
- [ ] All writes are audited (user is associated with record)
- [ ] Improve test coverage
- [ ] Run JMeter tests and publish performance metrics for a) write-intensive and b) read-intensive application

## 0.0.3

- [ ] Implement Vaadin-based administrative UI
- [ ] Docker Compose (or Kubernetes enable?); launch variant Docker image(s) sharing a single data-store (e.g., MySQL)
