CREATE TABLE entityav_iso_date (
	eid BINARY(16) NOT NULL,
	aid BINARY(16) NOT NULL,
	val DATE NOT NULL,
	created_time TIMESTAMP NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};
