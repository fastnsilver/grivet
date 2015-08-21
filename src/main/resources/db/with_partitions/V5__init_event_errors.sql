-- Table for storing any errors reported by the MySQL Events (see v4__init_events.sql)
CREATE TABLE `EventErrors` (
  `id` MEDIUMINT NOT NULL AUTO_INCREMENT,
  `errorDate` DATETIME NOT NULL,
  `sqlState` VARCHAR(256) NOT NULL,
  `errorMessage` VARCHAR(256) NOT NULL,
  `errorNo` VARCHAR(25) NOT NULL,
  `schemaName` VARCHAR(256) NULL,
  `tableName` VARCHAR(256) NULL,
  `eventName` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`, `errorDate`)
) ${createTableSuffix};