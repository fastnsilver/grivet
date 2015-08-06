
CREATE EVENT IF NOT EXISTS entity_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entity table 2 days ahead of the current day'
DO
  CALL CreateFuturePartition('entity', 2);
  
  
CREATE EVENT IF NOT EXISTS entityav_varchar_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_varchar table 2 days ahead of the current day'
DO
  CALL CreateFuturePartition('entityav_varchar', 2);
  

CREATE EVENT IF NOT EXISTS entityav_text_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_text table 2 days ahead of the current day'
DO
  CALL CreateFuturePartition('entityav_text', 2);
  
  
CREATE EVENT IF NOT EXISTS entityav_datetime_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_datetime table 2 days ahead of the current day'
DO
  CALL CreateFuturePartition('entityav_datetime', 2);
  
  
CREATE EVENT IF NOT EXISTS entityav_int_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_int table 2 days ahead of the current day'
DO
  CALL CreateFuturePartition('entityav_int', 2);
  
  
CREATE EVENT IF NOT EXISTS entityav_bigint_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_bigint table 2 days ahead of the current day'
DO
  CALL CreateFuturePartition('entityav_bigint', 2);
  

CREATE EVENT IF NOT EXISTS entityav_decimal_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_decimal table 2 days ahead of the current day'
DO
  CALL CreateFuturePartition('entityav_decimal', 2);
  
  
CREATE EVENT IF NOT EXISTS entityav_json_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_json table 2 days ahead of the current day'
DO
  CALL CreateFuturePartition('entityav_json', 2);
  
