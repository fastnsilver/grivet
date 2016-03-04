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

import com.fns.grivet.model.Attribute;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.EntityAttributeValue;
import com.fns.grivet.model.User;
import com.fns.grivet.model.ValueHelper;
import com.fns.grivet.query.DynamicQuery;
import com.fns.grivet.query.QueryBuilder;
import com.fns.grivet.service.SecurityFacade;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcEntityRepository implements EntityRepository {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired(required=false)
    private SecurityFacade securityFacade;
    
    @Autowired
    public JdbcEntityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Long id(Integer cid) {
        SimpleJdbcInsert insertEntity = new SimpleJdbcInsert(jdbcTemplate).withTableName("entity").usingGeneratedKeyColumns("eid").usingColumns("cid", "created_time");
        return Long.valueOf(String.valueOf(insertEntity.executeAndReturnKey(ImmutableMap.of("cid", cid, "created_time", Timestamp.valueOf(LocalDateTime.now())))));
    }

    @Override
    public void save(Long eid, Attribute attribute, AttributeType attributeType, Object rawValue) {
        Assert.isTrue(rawValue != null, String.format("Attempt to persist value failed! %s's value must not be null!", attribute.getName()));
        Object value = ValueHelper.toValue(attributeType, rawValue);
        User user = getCurrentUser();
        String createdBy = user != null ? user.getUsername() : null;
        String[] columns = { "eid", "aid", "val", "created_time" };
        Map<String, Object> keyValuePairs = ImmutableMap.of("eid", eid, "aid", attribute.getId(), "val", value, "created_time", Timestamp.valueOf(LocalDateTime.now()));
        if (createdBy != null) {
            columns = Arrays.copyOf(columns, columns.length + 1);
            columns[columns.length - 1] = "created_by";
            Map<String, Object> keyValuePairsWithCreatedBy = new HashMap<>(keyValuePairs);
            keyValuePairsWithCreatedBy.put("created_by", createdBy);
            keyValuePairs = keyValuePairsWithCreatedBy;
        } 
        SimpleJdbcInsert insertEntityAttributeValue = new SimpleJdbcInsert(jdbcTemplate).withTableName(String.join("_", "entityav", attributeType.getType())).usingColumns(columns);
        insertEntityAttributeValue.execute(keyValuePairs);
    }
    
    @Override
    public List<EntityAttributeValue> findByCreatedTime(Integer cid, LocalDateTime createdTimeStart,
            LocalDateTime createdTimeEnd) {
        String sql = QueryBuilder.newInstance().appendCreatedTimeRange().build();
        log.trace(String.format("JdbcEntityRepository.findByCreatedTime[sql=%s]", sql));
        SqlRowSet rowSet = jdbcTemplate.query(sql, new SqlRowSetResultSetExtractor(), new SqlParameterValue(Types.INTEGER, cid), new SqlParameterValue(Types.TIMESTAMP, Timestamp.valueOf(createdTimeStart)), new SqlParameterValue(Types.TIMESTAMP, Timestamp.valueOf(createdTimeEnd)));
        return mapRows(rowSet);
    }

    @Override
    public List<EntityAttributeValue> executeDynamicQuery(Integer cid, DynamicQuery query) {
        Assert.isTrue(query.areConjunctionsHomogenous(), "Query cannot be executed! All conjunctions must be homogenous!");
        String sql = QueryBuilder.newInstance().append(query).build();
        log.trace(String.format("JdbcEntityRepository.executeDynamicQuery[sql=%s]", sql));
        List<SqlParameterValue> values = new ArrayList<>();
        values.add(new SqlParameterValue(Types.INTEGER, cid));
        values.addAll(Arrays.asList(query.asSqlParameterValues()));
        SqlRowSet rowSet = jdbcTemplate.query(sql, new SqlRowSetResultSetExtractor(), values.toArray(new Object[values.size()]));
        return mapRows(rowSet);
    }
     
    private List<EntityAttributeValue> mapRows(SqlRowSet rowSet) {
        List<EntityAttributeValue> result = new ArrayList<>();
        EntityAttributeValue eav = null;
        if (rowSet != null) {
            while(rowSet.next()) {
                eav = new EntityAttributeValue((Long) rowSet.getObject("eid"), (Integer) rowSet.getObject("attribute_id"), (String) rowSet.getObject("attribute_name"), rowSet.getObject("attribute_value"), ((Timestamp) rowSet.getObject("created_time")).toLocalDateTime(), getCurrentUser());
                result.add(eav);
            }
        }
        Collections.sort(result, new EAVComparator());
        return result;
    }
    
    private User getCurrentUser() {
        return securityFacade != null ? securityFacade.getCurrentUser(): null;
    }
    
    private static class EAVComparator implements Comparator<EntityAttributeValue> {

        @Override
        public int compare(EntityAttributeValue eav1, EntityAttributeValue eav2) {
            return ObjectUtils.compare(eav1.getId(), eav2.getId());
        }
        
    }

}
