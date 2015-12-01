
-- Create a partition 1 day ahead of today's date (for each table) by ALTER-ing the following tables...

CALL CreateFuturePartitions('entity', 2);
CALL CreateFuturePartitions('entityav_varchar', 2);
CALL CreateFuturePartitions('entityav_text', 2);
CALL CreateFuturePartitions('entityav_datetime', 2);
CALL CreateFuturePartitions('entityav_int', 2);
CALL CreateFuturePartitions('entityav_bigint', 2);
CALL CreateFuturePartitions('entityav_decimal', 2);
CALL CreateFuturePartitions('entityav_json', 2);


-- Drop the initially created partition for each table...

CALL DropNamedPartition('entity', 'p0');
CALL DropNamedPartition('entityav_varchar', 'p0');
CALL DropNamedPartition('entityav_text', 'p0');
CALL DropNamedPartition('entityav_datetime', 'p0');
CALL DropNamedPartition('entityav_int', 'p0');
CALL DropNamedPartition('entityav_bigint', 'p0');
CALL DropNamedPartition('entityav_decimal', 'p0');
CALL DropNamedPartition('entityav_json', 'p0');
