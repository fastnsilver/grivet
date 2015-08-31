# Grivet 
[![Build Status](https://travis-ci.org/fastnsilver/grivet.svg)](https://travis-ci.org/fastnsilver/grivet)
[![Coverage Status](https://coveralls.io/repos/fastnsilver/grivet/badge.svg?branch=master&service=github)](https://coveralls.io/github/fastnsilver/grivet?branch=master)

[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![endorse](https://api.coderwall.com/fastnsilver/endorsecount.png)](https://coderwall.com/fastnsilver)

A micro-service capable of self-service modeling and persistence.


# Motivation

Designing, maintaining and growing a database schema to store and retrieve domain objects is tedious. Yes, there are options like: [ORM](http://www.yegor256.com/2014/12/01/orm-offensive-anti-pattern.html) via [JPA](https://jcp.org/aboutJava/communityprocess/final/jsr338/index.html), [JDO](https://jcp.org/aboutJava/communityprocess/mrel/jsr243/index3.html) or [jOOQ](http://www.jooq.org/); or document object stores like [Couchbase](http://www.couchbase.com/nosql-databases/couchbase-server) or [Mongo](https://docs.mongodb.org/manual/).  Without having you jump through too many hoops, `Grivet` provides a simple API backed by a RDBMS-based abstraction for type registry, storage and retrieval. 

Make it easy to query types.  E.g., when querying...

* Define a pipe-separated list of constraint keyed parameter values, specified in the form `c=<attribute_name>|<operator>|<value_or_comma_separated_values>|<optional_conjunction>`
* Reference a named query that was (previously) registered plus optional parameter key-value pairs 

to express filtering constraints where constraints are applied on a GET request for a type.


# Documentation

* [Developer Notes](docs/DEV_NOTES.md)
* [Project Info](http://fastnsilver.github.io/grivet/project-info.html)
* [Service Endpoints](docs/ENDPOINTS.md)
* [Sample Workflow](docs/WORKFLOW.md)
* [Roadmap](docs/ROADMAP.md)
