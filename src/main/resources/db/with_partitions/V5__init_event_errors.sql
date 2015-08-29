-- Table for storing any errors reported by the MySQL Events (see v4__init_events.sql)
CREATE TABLE `event_errors` (
  `id` MEDIUMINT NOT NULL AUTO_INCREMENT,
  `error_date` DATETIME NOT NULL,
  `sql_state` VARCHAR(256) NOT NULL,
  `error_message` VARCHAR(256) NOT NULL,
  `error_no` VARCHAR(25) NOT NULL,
  `schema_name` VARCHAR(256) NULL,
  `table_name` VARCHAR(256) NULL,
  `event_name` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`, `error_date`)
) ${createTableSuffix};