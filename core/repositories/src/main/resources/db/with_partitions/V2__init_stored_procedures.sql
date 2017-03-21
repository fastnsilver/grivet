-- START: Add partitions to a table

DELIMITER $$
DROP PROCEDURE IF EXISTS CreatePreviousPartitions $$
CREATE PROCEDURE CreatePreviousPartitions (in tableName VARCHAR(36), in numDays INT)

  BEGIN
    DECLARE crs INT DEFAULT numDays;
    WHILE crs > 0 DO
      SET @stmt := CONCAT(
          'ALTER TABLE ',
          tableName,
          ' ADD PARTITION ('
          ,   'PARTITION p'
          ,   DATE_FORMAT(DATE(NOW() - INTERVAL crs DAY) + INTERVAL 0 SECOND, '%Y%m%d%H%i%S')
          ,   ' VALUES LESS THAN ('
          ,   QUOTE(DATE_FORMAT(DATE(NOW() - INTERVAL crs DAY) + INTERVAL 0 SECOND, '%Y-%m-%d %T'))
          ,   '))'
      );
      PREPARE stmt FROM @stmt;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;
      SET crs = crs - 1;
    END WHILE;
  END $$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS CreateFuturePartition $$
CREATE PROCEDURE CreateFuturePartition (in tableName VARCHAR(36), in daysAhead INT)

  BEGIN
	  SET @stmt := CONCAT(
	      'ALTER TABLE ',
	      tableName,
	      ' ADD PARTITION ('
	      ,   'PARTITION p'
	      ,   DATE_FORMAT(DATE(NOW() + INTERVAL daysAhead DAY) + INTERVAL 0 SECOND, '%Y%m%d%H%i%S')
	      ,   ' VALUES LESS THAN ('
	      ,   QUOTE(DATE_FORMAT(DATE(NOW() + INTERVAL daysAhead DAY) + INTERVAL 0 SECOND, '%Y-%m-%d %T'))
	      ,   '))'
	  );
	  PREPARE stmt FROM @stmt;
	  EXECUTE stmt;
	  DEALLOCATE PREPARE stmt;
  END $$
DELIMITER ;

DELIMITER $$
DROP PROCEDURE IF EXISTS CreateFuturePartitions $$
CREATE PROCEDURE CreateFuturePartitions (in tableName VARCHAR(36), in numDays INT)

  BEGIN
    DECLARE crs INT DEFAULT 0;
    WHILE crs < numDays DO
      SET @stmt := CONCAT(
          'ALTER TABLE ',
          tableName,
          ' ADD PARTITION ('
          ,   'PARTITION p'
          ,   DATE_FORMAT(DATE(NOW() + INTERVAL crs DAY) + INTERVAL 0 SECOND, '%Y%m%d%H%i%S')
          ,   ' VALUES LESS THAN ('
          ,   QUOTE(DATE_FORMAT(DATE(NOW() + INTERVAL crs DAY) + INTERVAL 0 SECOND, '%Y-%m-%d %T'))
          ,   '))'
      );
      PREPARE stmt FROM @stmt;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;
      SET crs = crs + 1;
    END WHILE;
  END $$
DELIMITER ;

-- END: Add partitions to a table

-- START: Convenience function for dropping a named partition from a table

DELIMITER $$
CREATE PROCEDURE DropNamedPartition(in tableName VARCHAR(36), in partitionName VARCHAR(9))
  BEGIN
    SET @stmt := CONCAT(
        'ALTER TABLE ',
        tableName,
        ' DROP PARTITION '
        , partitionName
    );
    PREPARE stmt FROM @stmt;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END $$
DELIMITER ;
