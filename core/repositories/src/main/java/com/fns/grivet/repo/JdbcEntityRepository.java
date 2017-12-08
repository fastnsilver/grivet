/*
 * Copyright 2015 - Chris Phillipson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fns.grivet.repo;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.EntityAttributeValue;
import com.fns.grivet.model.User;
import com.fns.grivet.model.ValueHelper;
import com.fns.grivet.query.DynamicQuery;
import com.fns.grivet.query.QueryBuilder;
import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class JdbcEntityRepository implements EntityRepository {

	private final JdbcTemplate jdbcTemplate;

	@Autowired(required=false)
	private AuditorProvider auditorProvider;

	@Autowired
	public JdbcEntityRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	@Override
	public Long newId(Integer cid, LocalDateTime createdTime) {
		return Long.valueOf(String.valueOf(new SimpleJdbcInsert(jdbcTemplate).withTableName("entity")
				.usingGeneratedKeyColumns("eid").usingColumns("cid", "created_time").executeAndReturnKey(
						ImmutableMap.of("cid", cid, "created_time", Timestamp.valueOf(createdTime)))));
	}

	@Override
	public void save(Long eid, Attribute attribute, AttributeType attributeType, Object rawValue,
			LocalDateTime createdTime) {
		Assert.isTrue(rawValue != null, String.format("Attempt to persist value failed! %s's value must not be null!", attribute.getName()));
		Object value = ValueHelper.toValue(attributeType, rawValue);
		String createdBy = getCurrentUsername();
		String[] columns = { "eid", "aid", "val", "created_time" };
		Map<String, Object> keyValuePairs = ImmutableMap.of("eid", eid, "aid", attribute.getId(), "val", value,
				"created_time", Timestamp.valueOf(createdTime));
		if (createdBy != null) {
			columns = Arrays.copyOf(columns, columns.length + 1);
			columns[columns.length - 1] = "created_by";
			Map<String, Object> keyValuePairsWithCreatedBy = new HashMap<>(keyValuePairs);
			keyValuePairsWithCreatedBy.put("created_by", createdBy);
			keyValuePairs = keyValuePairsWithCreatedBy;
		}
		new SimpleJdbcInsert(jdbcTemplate).withTableName(String.join("_", "entityav", attributeType.getType()))
		.usingColumns(columns).execute(keyValuePairs);
	}

	@Override
	public List<EntityAttributeValue> findByCreatedTime(Integer cid, LocalDateTime createdTimeStart,
			LocalDateTime createdTimeEnd) {
		String sql = QueryBuilder.newInstance().appendCreatedTimeRange().build();
		log.trace(String.format("JdbcEntityRepository.findByCreatedTime[sql=%s]", sql));
		return mapRows(jdbcTemplate.query(sql, new SqlRowSetResultSetExtractor(), new SqlParameterValue(Types.INTEGER, cid),
				new SqlParameterValue(Types.TIMESTAMP, Timestamp.valueOf(createdTimeStart)),
				new SqlParameterValue(Types.TIMESTAMP, Timestamp.valueOf(createdTimeEnd))));
	}

	@Override
	public Integer getClassIdForEntityId(Long eid) {
		String sql = "SELECT cid FROM entity WHERE eid = ?";
		log.trace(String.format("JdbcEntityRepository.getClassIdForEntityId[sql=%s]", sql));
		return jdbcTemplate.queryForObject(sql, new Object[] { eid }, Integer.class);
	}

	@Override
	public List<EntityAttributeValue> findByEntityId(Long eid) {
		String sql = QueryBuilder.newInstance().obtainValuesForOneEntity().build();
		log.trace(String.format("JdbcEntityRepository.findById[sql=%s]", sql));
		return mapRows(
				jdbcTemplate.query(sql, new SqlRowSetResultSetExtractor(),
						new SqlParameterValue(Types.BIGINT, eid)));
	}

	@Override
	public void delete(Long eid) {
		String entitySql = "DELETE FROM entity WHERE eid = ?";
		log.trace(String.format("JdbcEntityRepository.delete[sql=%s]", entitySql));
		jdbcTemplate.update(entitySql, new Object[] { eid });

		Collection<String> eavSql =
				Stream.of(AttributeType.values())
						.collect(Collectors.toMap(k -> k.getType(),
								v -> String.format("DELETE FROM entityav_%s WHERE eid = ?", v.getType())))
							.values();
		for (String sql : eavSql) {
			log.trace(String.format("JdbcEntityRepository.delete[sql=%s]", sql));
			jdbcTemplate.update(sql, new Object[] { eid });
		}
	}
	
	@Override
	public void deleteAll() {
	    String entitySql = "DELETE FROM entity";
        log.trace(String.format("JdbcEntityRepository.delete[sql=%s]", entitySql));
        jdbcTemplate.execute(entitySql);

        Collection<String> eavSql =
                Stream.of(AttributeType.values())
                        .collect(Collectors.toMap(k -> k.getType(),
                                v -> String.format("DELETE FROM entityav_%s", v.getType())))
                            .values();
        for (String sql : eavSql) {
            log.trace(String.format("JdbcEntityRepository.delete[sql=%s]", sql));
            jdbcTemplate.execute(sql);
        }
	}

	@Override
	public List<EntityAttributeValue> findAllEntitiesByCid(Integer cid) {
		String sql = QueryBuilder.newInstance().obtainValuesForEntitiesByCid().build();
		log.trace(String.format("JdbcEntityRepository.findAllByCid[sql=%s]", sql));
		return mapRows(
				jdbcTemplate.query(sql, new SqlRowSetResultSetExtractor(), new SqlParameterValue(Types.INTEGER, cid)));
	}

	@Override
	public List<EntityAttributeValue> executeDynamicQuery(Integer cid, DynamicQuery query) {
		Assert.isTrue(query.areConjunctionsHomogenous(), "Query cannot be executed! All conjunctions must be homogenous!");
		String sql = QueryBuilder.newInstance().append(query).build();
		log.trace(String.format("JdbcEntityRepository.executeDynamicQuery[sql=%s]", sql));
		List<SqlParameterValue> values = new ArrayList<>();
		values.add(new SqlParameterValue(Types.INTEGER, cid));
		values.addAll(Arrays.asList(query.asSqlParameterValues()));
		return mapRows(jdbcTemplate.query(sql, new SqlRowSetResultSetExtractor(), values.toArray(new Object[values.size()])));
	}

	private List<EntityAttributeValue> mapRows(SqlRowSet rowSet) {
		List<EntityAttributeValue> result = new ArrayList<>();
		EntityAttributeValue eav = null;
		if (rowSet != null) {
			while(rowSet.next()) {
				eav = EntityAttributeValue.of(
				        (Long) rowSet.getObject("eid"), 
				        (Integer) rowSet.getObject("attribute_id"), 
				        (String) rowSet.getObject("attribute_name"), 
				        (String) rowSet.getObject("attribute_value"), 
				        ((Timestamp) rowSet.getObject("created_time")).toLocalDateTime(), 
				        getCurrentUsername());
				result.add(eav);
			}
		}
		Collections.sort(result, new EAVComparator());
		return result;
	}

	private String getCurrentUsername() {
	    String username = null;
	    User principal = auditorProvider == null ? null : auditorProvider.getCurrentUser();
	    if (principal != null) {
	        username = principal.getUsername();
	    }
	    return username;
	}

	private static class EAVComparator implements Comparator<EntityAttributeValue> {

		@Override
		public int compare(EntityAttributeValue eav1, EntityAttributeValue eav2) {
			return ObjectUtils.compare(eav1.getId(), eav2.getId());
		}

	}

}
