DROP VIEW all_entity_values;


ALTER TABLE class ADD created_by INT DEFAULT NULL;

ALTER TABLE class ADD updated_by INT DEFAULT NULL;


ALTER TABLE attribute ADD created_by INT DEFAULT NULL;

ALTER TABLE attribute ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE attribute ADD updated_by INT DEFAULT NULL;


ALTER TABLE class_attribute ADD created_by INT DEFAULT NULL;

ALTER TABLE class_attribute ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE class_attribute ADD updated_by INT DEFAULT NULL;


ALTER TABLE entity ADD created_by INT DEFAULT NULL; 


ALTER TABLE entityav_varchar ADD created_by INT DEFAULT NULL;


ALTER TABLE entityav_text ADD created_by INT DEFAULT NULL;  	


ALTER TABLE entityav_datetime ${renameTableKeyword} entityav_iso_datetime;

ALTER TABLE entityav_iso_datetime ADD created_by INT DEFAULT NULL;


ALTER TABLE entityav_iso_date ADD created_by INT DEFAULT NULL;


ALTER TABLE entityav_iso_instant ADD created_by INT DEFAULT NULL;


ALTER TABLE entityav_int ADD created_by INT DEFAULT NULL;


ALTER TABLE entityav_bigint ADD created_by INT DEFAULT NULL;


ALTER TABLE entityav_decimal ADD created_by INT DEFAULT NULL;


ALTER TABLE entityav_json ADD created_by INT DEFAULT NULL;


ALTER TABLE named_query ADD created_by INT DEFAULT NULL;

ALTER TABLE named_query ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE named_query ADD updated_by INT DEFAULT NULL;


ALTER TABLE named_query_parameter ${alterColumnKeyword} parameter_type VARCHAR(20);
