DROP VIEW recent_entity_values;

DROP VIEW all_entity_values;

CREATE VIEW all_entity_values AS 
    SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time, created_by FROM entityav_json
    UNION
    SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time, created_by FROM entityav_text
    UNION
    SELECT eid, aid, ${dateFormatFn} AS v, created_time, created_by FROM entityav_iso_date
    UNION
    SELECT eid, aid, ${dateTimeFormatFn} AS v, created_time, created_by FROM entityav_iso_datetime
    UNION
    SELECT eid, aid, ${instantFormatFn} AS v, created_time, created_by FROM entityav_iso_instant
    UNION
    SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time, created_by FROM entityav_decimal
    UNION
    SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time, created_by FROM entityav_varchar
    UNION
    SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time, created_by FROM entityav_bigint
    UNION
    SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time, created_by FROM entityav_int
    UNION
    SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time, created_by FROM entityav_boolean;
    
CREATE VIEW recent_entity_values AS
    SELECT v1.*
    FROM all_entity_values v1
    WHERE v1.created_time = (
        SELECT MAX(v2.created_time) 
        FROM all_entity_values v2
        WHERE v2.eid = v1.eid AND v2.aid = v2.aid
    )