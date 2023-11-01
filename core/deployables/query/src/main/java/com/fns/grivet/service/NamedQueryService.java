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
package com.fns.grivet.service;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.CallableStatementCreatorFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlRowSetResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.repo.NamedQueryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NamedQueryService {
    
    private final NamedQueryRepository namedQueryRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ObjectMapper mapper;
    
    @Autowired
    public NamedQueryService(NamedQueryRepository namedQueryRepository, JdbcTemplate jdbcTemplate, ObjectMapper mapper) {
        this.namedQueryRepository = namedQueryRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.mapper = mapper;
    }
    
    @Transactional
    public void create(NamedQuery namedQuery) {
        namedQueryRepository.save(namedQuery);
    }
    
    @Transactional(readOnly=true)
    public String get(String name, MultiValueMap<String, ?> parameters) {
        NamedQuery namedQuery = namedQueryRepository.findByName(name);
        Assert.notNull(namedQuery, "No query found that matches name [%s]!".formatted(name));
        MapSqlParameterSource parameterSource = namedQuery.asParameterSource(parameters);
        String sql = namedQuery.getQuery();
        Map<String, String> sqlParams = namedQuery.getParams();
        SqlRowSet rowSet = null;
        if (parameterSource != null) {
            // check that all params were supplied...
            Set<String> queryParamKeys = sqlParams.keySet();
            Set<String> parametersKeys = parameters.keySet();
            Assert.isTrue(parametersKeys.containsAll(queryParamKeys), "Query cannot be executed! Missing query parameters!");
            switch (namedQuery.getType()) {
                case SELECT:
                    rowSet = namedParameterJdbcTemplate.queryForRowSet(sql, parameterSource);
                    break;
                case SPROC:  
                    String sproc = getProcedure(sql, queryParamKeys);
                    CallableStatementCreatorFactory factory = new CallableStatementCreatorFactory(sproc, namedQuery.asSqlParameters(parameters));
                    CallableStatementCreator csc = factory.newCallableStatementCreator(parameterSource.getValues());
                    rowSet = callSproc(csc);
                    break;
            }
        } else {
            Assert.isTrue(sqlParams.isEmpty(), "Query cannot be executed! Query expects parameters!");
            // ignore any supplied request params
            switch (namedQuery.getType()) {
                case SELECT:
                    rowSet = namedParameterJdbcTemplate.getJdbcOperations().query(sql, new SqlRowSetResultSetExtractor());
                    break;
                case SPROC:
                    String sproc = getProcedure(sql, null);
                    CallableStatementCreatorFactory factory = new CallableStatementCreatorFactory(sproc, namedQuery.asSqlParameters(parameters));
                    CallableStatementCreator csc = factory.newCallableStatementCreator((Map<String, ?>) null);
                    rowSet = callSproc(csc);
                    break;
            }
        }
        return mapRows(rowSet);
    }


    @Transactional
    public void delete(String name) {
        NamedQuery nq = namedQueryRepository.findByName(name);
        if (nq != null) {
            namedQueryRepository.delete(nq);
        }
    }
    
    protected SqlRowSet callSproc(CallableStatementCreator csc) {
       return jdbcTemplate.execute(csc, new CallableStatementCallback<SqlRowSet>() {
            
            @Override
            public SqlRowSet doInCallableStatement(CallableStatement cs)
                    throws SQLException, DataAccessException {
                log.debug("Stored procedure to be executed: " + cs.toString());
                return new SqlRowSetResultSetExtractor().extractData(cs.executeQuery());
            }
            
        });
    }
    
    protected String mapRows(SqlRowSet rowSet) {
        JSONArray jsonArray = new JSONArray();
        if (rowSet != null) {
            String[] columnNames = rowSet.getMetaData().getColumnNames();
            JSONObject jsonObject = null;
            while(rowSet.next()) {
                jsonObject = new JSONObject();
                for (String columnName: columnNames) {
                    jsonObject.put(columnName, rowSet.getObject(columnName));
                }
                jsonArray.put(jsonObject);
            }
        }
        return jsonArray.toString();
    }
    
    private String getProcedure(String sql, Set<String> queryParamKeys) {
        if (!CollectionUtils.isEmpty(queryParamKeys)) {
            for (String k: queryParamKeys) {
               sql =  sql.replace(":" + k, "?");
            }
        }
        if (!sql.startsWith("{")) {
            sql = "%s%s".formatted("{", sql);
        }
        if (!sql.endsWith("}")) {
            sql = "%s%s".formatted(sql, "}");
        }
        return sql;
    }
    
    @Transactional(readOnly=true)
    public JSONArray all() {
        JSONArray result = new JSONArray();
        Iterable<NamedQuery> iterable = namedQueryRepository.findAll();
        Assert.notNull(iterable, "No named queries are registered!");
        Iterator<NamedQuery> it = iterable.iterator();
        NamedQuery nq = null;
        JSONObject jo = null;
        JsonNode jn = null;
        while(it.hasNext()) {
           nq = it.next();
           jn = mapper.valueToTree(nq);
           jo = new JSONObject(jn.toString());
           jo.remove("id");
           jo.remove("createdTime");
           result.put(jo);
        }
        return result;
    }
}
