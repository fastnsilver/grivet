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
	SELECT eid, aid, CAST(val AS ${castedColumnType}) AS v, created_time, created_by FROM entityav_int;