ALTER TABLE class ADD created_by INT DEFAULT NULL;

ALTER TABLE class ADD updated_by INT DEFAULT NULL;

ALTER TABLE attribute ADD created_by INT DEFAULT NULL;

ALTER TABLE attribute ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE attribute ADD updated_by INT DEFAULT NULL;

ALTER TABLE class_attribute ADD created_by INT DEFAULT NULL;

ALTER TABLE class_attribute ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE class_attribute ADD updated_by INT DEFAULT NULL;

ALTER TABLE entity ADD created_by INT DEFAULT NULL; 

ALTER TABLE entity ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE entity ADD updated_by INT DEFAULT NULL;

ALTER TABLE entityav_varchar ADD created_by INT DEFAULT NULL;

ALTER TABLE entityav_varchar ADD updated_time DATETIME DEFAULT NULL;  

ALTER TABLE entityav_varchar ADD updated_by INT DEFAULT NULL ;

ALTER TABLE entityav_text ADD created_by INT DEFAULT NULL;  	

ALTER TABLE entityav_text ADD updated_time DATETIME DEFAULT NULL;  

ALTER TABLE entityav_text ADD updated_by INT DEFAULT NULL;

ALTER TABLE entityav_datetime ADD created_by INT DEFAULT NULL;

ALTER TABLE entityav_datetime ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE entityav_datetime ADD updated_by INT DEFAULT NULL;

ALTER TABLE entityav_int ADD created_by INT DEFAULT NULL;

ALTER TABLE entityav_int ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE entityav_int ADD updated_by INT DEFAULT NULL;

ALTER TABLE entityav_bigint ADD created_by INT DEFAULT NULL;

ALTER TABLE entityav_bigint ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE entityav_bigint ADD updated_by INT DEFAULT NULL;

ALTER TABLE entityav_decimal ADD created_by INT DEFAULT NULL;

ALTER TABLE entityav_decimal ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE entityav_decimal ADD updated_by INT DEFAULT NULL;

ALTER TABLE entityav_json ADD created_by INT DEFAULT NULL;

ALTER TABLE entityav_json ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE entityav_json ADD updated_by INT DEFAULT NULL;

ALTER TABLE named_query ADD created_by INT DEFAULT NULL;

ALTER TABLE named_query ADD updated_time DATETIME DEFAULT NULL;

ALTER TABLE named_query ADD updated_by INT DEFAULT NULL;
