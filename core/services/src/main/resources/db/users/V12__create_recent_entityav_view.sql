CREATE VIEW recent_entity_values AS
    SELECT v1.*
    FROM all_entity_values v1
    WHERE v1.created_time = (
        SELECT MAX(v2.created_time) 
        FROM all_entity_values v2
        WHERE v2.eid = v1.eid AND v2.aid = v2.aid
    )