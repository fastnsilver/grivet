-- Drop existing event and re-create with error logging
DROP EVENT entity_add_daily_partition;
DROP EVENT entityav_varchar_add_daily_partition;
DROP EVENT entityav_text_add_daily_partition;
DROP EVENT entityav_datetime_add_daily_partition;
DROP EVENT entityav_int_add_daily_partition;
DROP EVENT entityav_bigint_add_daily_partition;
DROP EVENT entityav_decimal_add_daily_partition;
DROP EVENT entityav_json_add_daily_partition;

DELIMITER |

CREATE EVENT entity_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entity table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQL_STATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entity_add_daily_partition');
  END;
  CALL CreateFuturePartition('entity', 2);
END |
  
  
CREATE EVENT entityav_varchar_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_varchar table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQL_STATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entityav_varchar_add_daily_partition');
  END;
  CALL CreateFuturePartition('entityav_varchar', 2);
END | 

CREATE EVENT entityav_text_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_text table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQL_STATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entityav_text_add_daily_partition');
  END;
  CALL CreateFuturePartition('entityav_text', 2);
END | 
  
CREATE EVENT entityav_datetime_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_datetime table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQL_STATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entityav_datetime_add_daily_partition');
  END;
  CALL CreateFuturePartition('entityav_datetime', 2);
END |  
  
CREATE EVENT entityav_int_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_int table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQL_STATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entityav_int_add_daily_partition');
  END;
  CALL CreateFuturePartition('entityav_int', 2);
END | 
  
CREATE EVENT entityav_bigint_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_bigint table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQL_STATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entityav_bigint_add_daily_partition');
  END;
  CALL CreateFuturePartition('entityav_bigint', 2);
END | 

CREATE EVENT entityav_decimal_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_decimal table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQL_STATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entityav_decimal_add_daily_partition');
  END;
  CALL CreateFuturePartition('entityav_decimal', 2);
END |  
  
CREATE EVENT entityav_json_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_json table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQL_STATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entityav_json_add_daily_partition');
  END;
  CALL CreateFuturePartition('entityav_json', 2);
END |

DELIMITER ;
