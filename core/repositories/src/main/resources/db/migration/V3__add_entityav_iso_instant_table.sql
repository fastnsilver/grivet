CREATE TABLE entityav_iso_instant (
	eid BIGINT NOT NULL,
	aid INT NOT NULL,
	val TIMESTAMP(9) NOT NULL,
	created_time TIMESTAMP(9) NOT NULL,
	PRIMARY KEY (eid, aid, created_time),
	FOREIGN KEY (eid) REFERENCES entity(eid) ON DELETE CASCADE,
	FOREIGN KEY (aid) REFERENCES attribute(id)
) ${createTableSuffix};
