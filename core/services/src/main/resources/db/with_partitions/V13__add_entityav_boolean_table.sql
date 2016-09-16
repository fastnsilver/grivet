CREATE TABLE entityav_boolean (
    eid BIGINT NOT NULL,
    aid INT NOT NULL,
    val TINYINT NOT NULL,
    created_time DATETIME(3) NOT NULL,
    PRIMARY KEY (eid, aid, created_time)
) ${createTableSuffix}
PARTITION BY RANGE COLUMNS(created_time) (
  PARTITION p0 VALUES LESS THAN ('1970-07-14 00:00:00')
);

CALL CreateFuturePartitions('entityav_boolean', 2);
CALL DropNamedPartition('entityav_boolean', 'p0');

DELIMITER |

CREATE EVENT entityav_boolean_add_daily_partition
  ON SCHEDULE
    EVERY 1 DAY STARTS CURDATE()
  COMMENT 'Adds daily partition to entityav_boolean table 2 days ahead of the current day'
DO
  BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      ROLLBACK;
      GET DIAGNOSTICS condition 1
      @p1 = MESSAGE_TEXT,
      @p2 = RETURNED_SQLSTATE,
      @p3 = MYSQL_ERRNO,
      @p4 = SCHEMA_NAME,
      @p5 = TABLE_NAME;
    INSERT INTO event_errors (`error_date`, `error_message`, `sql_state`, `error_no`, `schema_name`, `table_name`, `event_name`)
    VALUES (NOW(), @p1, @p2, @p3, @p4, @p5, 'entityav_boolean_add_daily_partition');
  END;
  CALL CreateFuturePartition('entityav_boolean', 2);
END |  

DELIMITER ;
