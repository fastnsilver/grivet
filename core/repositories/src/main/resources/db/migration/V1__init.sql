${preamble}

CREATE TABLE class (
    id INT ${autoInc},
    version INT,
    name VARCHAR(255) ${varcharQualifier} NOT NULL UNIQUE,
    description VARCHAR(1000) ${varcharQualifier},
    validatable BOOLEAN,
    json_schema VARCHAR(2000) ${varcharQualifier},
    created_time TIMESTAMP(9) NOT NULL,
    updated_time TIMESTAMP(9) NOT NULL,
    PRIMARY KEY (id)
) ${createTableSuffix};

CREATE TABLE attribute (
    id INT ${autoInc},
    version INT,
    name VARCHAR(255) ${varcharQualifier} NOT NULL UNIQUE,
    description VARCHAR(1000) ${varcharQualifier},
    created_time TIMESTAMP(9) NOT NULL,
    PRIMARY KEY (id)
) ${createTableSuffix};

CREATE TABLE class_attribute (
	cid INT NOT NULL,
	aid INT NOT NULL,
	tid INT NOT NULL,
	version INT,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (cid, aid, tid),
	FOREIGN KEY (cid) REFERENCES class(id) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};

CREATE TABLE entity (
	cid INT NOT NULL,
	eid BIGINT ${autoInc},
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid),
	FOREIGN KEY (cid) REFERENCES class(id) ON DELETE CASCADE
) ${createTableSuffix};

CREATE TABLE entityav_varchar (
	eid BIGINT NOT NULL,
	aid INT NOT NULL,
	val VARCHAR(255) ${varcharQualifier} NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};

CREATE TABLE entityav_text (
	eid BIGINT NOT NULL,
	aid INT NOT NULL,
	val ${longTextColumnType} NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};


CREATE TABLE entityav_datetime (
	eid BIGINT NOT NULL,
	aid INT NOT NULL,
	val DATETIME NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};

CREATE TABLE entityav_int (
	eid BIGINT NOT NULL,
	aid INT NOT NULL,
	val INT NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};

CREATE TABLE entityav_bigint (
	eid BIGINT NOT NULL,
	aid INT NOT NULL,
	val BIGINT NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};

CREATE TABLE entityav_decimal (
	eid BIGINT NOT NULL,
	aid INT NOT NULL,
	val DECIMAL(20,5) NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};

CREATE TABLE entityav_json (
	eid BIGINT NOT NULL,
	aid INT NOT NULL,
	val ${textColumnType} NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};

CREATE VIEW all_entity_values AS 
	SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time FROM entityav_json
	UNION
	SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time FROM entityav_text
	UNION
	SELECT eid, aid, ${dateTimeFormatFn} AS v, created_time FROM entityav_datetime
	UNION
	SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time FROM entityav_decimal
	UNION
	SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time FROM entityav_varchar
	UNION
	SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time FROM entityav_bigint
	UNION
	SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time FROM entityav_int;

CREATE TABLE named_query (
	id INT ${autoInc},
	version INT,
	name VARCHAR(255) ${varcharQualifier} NOT NULL UNIQUE,
	type VARCHAR(6) NOT NULL,
	query VARCHAR(2000) NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
    PRIMARY KEY (id)
) ${createTableSuffix};

CREATE TABLE named_query_parameter (
	id INT NOT NULL,
    parameter_name VARCHAR(50) ${varcharQualifier} NOT NULL,
    parameter_type VARCHAR(10) ${varcharQualifier} NOT NULL,
    PRIMARY KEY (id, parameter_name),
    FOREIGN KEY (id) REFERENCES named_query(id) ON DELETE CASCADE
) ${createTableSuffix};