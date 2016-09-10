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
package com.fns.grivet.query;

import com.fns.grivet.model.AttributeType;

import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class DynamicQuery {

    private final List<Constraint> constraints = new ArrayList<>();
    private final Map<Integer, Integer> attributeToAttributeTypeMap;
    private final Map<String, Integer> attributeNameToAttributeIdMap;
    
    public DynamicQuery(Set<Entry<String, String[]>> requestParams, 
            Map<Integer, Integer> attributeToAttributeTypeMap, 
            Map<String, Integer> attributeNameToAttributeIdMap) {
        String k = null;
        String[] v = null;
        if (requestParams != null && !requestParams.isEmpty()) {
            for (Entry<String, String[]> requestParam : requestParams) {
                k = requestParam.getKey();
                v = requestParam.getValue();
                if (isConstraintKey(k)) {
                    for (String cs: v) {
                        if (isConstraintValue(cs)) {
                            constraints.add(new Constraint(cs.split("\\|")));
                        }
                    }
                }
            }
        }
        this.attributeToAttributeTypeMap = attributeToAttributeTypeMap;
        this.attributeNameToAttributeIdMap = attributeNameToAttributeIdMap;
    }
    
    public boolean hasConstraints() {
        return constraints.size() > 0;
    }
    
    List<Constraint> getConstraints() {
        return Collections.unmodifiableList(constraints);     
    }
    
    public boolean containsAndConjunction() {
        return constraints
                .stream()
                .filter(c -> Objects.equals(c.getConjunction(), Conjunction.AND))
                .collect(Collectors.toList()).size() >= 1;
    }
    
    public boolean areConjunctionsHomogenous() {
        boolean result = true;
        if (constraints.size() >=1) {
            Conjunction current = null;
            Conjunction prior = null;
            for (Constraint c: constraints) {
                current = c.getConjunction();
                if (current!= null && prior != null && current != prior) {
                    result = false;
                    break;
                }
                prior = current;
            }
        }
        return result;
    }
    
    public SqlParameterValue[] asSqlParameterValues() {
        List<SqlParameterValue> paramValues = new ArrayList<>();
        Integer attributeId = null;
        Integer attributeTypeId = null;
        AttributeType at = null;
        String attributeName = null;
        for (Constraint c: constraints) {
            attributeName = c.getAttributeName();
            Assert.isTrue(attributeName !=null && !attributeName.isEmpty(), "Attribute name must not be null or empty!");
            attributeId = attributeNameToAttributeIdMap.get(c.getAttributeName());
            Assert.notNull(attributeId, String.format("Invalid query constraint! No attribute identifier found for [%s]", attributeName));
            paramValues.add(new SqlParameterValue(Types.INTEGER, attributeId));
            attributeTypeId = attributeToAttributeTypeMap.get(attributeId);
            Assert.notNull(attributeTypeId, String.format("Invalid query constraint! No attribute type identifier found for attribute name [%s]", attributeName));
            at = AttributeType.fromId(attributeTypeId);
            Assert.notNull(at, String.format("Invalid query constraint! No matching attribute type found for attribute type identifier [%d]", attributeTypeId));
            if (c.getOperator().equals(Operator.BETWEEN)) {
                paramValues.add(getSqlParameterValue(at, c.getOperator(), c.getValues()[0]));
                paramValues.add(getSqlParameterValue(at, c.getOperator(), c.getValues()[1]));
            } else if (c.getOperator().equals(Operator.IN)) {
                for (String value: c.getValues()) {
                    paramValues.add(getSqlParameterValue(at, c.getOperator(), value));
                }
            } else {
                paramValues.add(getSqlParameterValue(at, c.getOperator(), c.getValues()[0]));
            }
        }
        return paramValues.toArray(new SqlParameterValue[paramValues.size()]);
    }
    
    private boolean isConstraintKey(String key) {
        boolean result = false;
        if ((key.equalsIgnoreCase("c") || key.equalsIgnoreCase("constraint"))) {
            result = true;
        }
        return result;
    }
    
    private boolean isConstraintValue(String value) {
        boolean result = false;
        if (value != null && StringUtils.countOccurrencesOf(value,"|") >= 2) {
            result = true;
        }
        return result;
    }
    
    protected SqlParameterValue getSqlParameterValue(AttributeType at, Operator op, String value) {
        SqlParameterValue result = null;
        switch (at) {
            case BIG_INTEGER:
                result = new SqlParameterValue(at.getSqlType(), Long.valueOf(value));
                break;
            case ISO_DATE:
                result = new SqlParameterValue(at.getSqlType(), java.sql.Date.valueOf(LocalDate.parse(value)));
                break;
            case ISO_DATETIME:
                result = new SqlParameterValue(at.getSqlType(), Timestamp.valueOf(LocalDateTime.parse(value)));
                break;
            case ISO_INSTANT:
                result = new SqlParameterValue(at.getSqlType(), Timestamp.from(Instant.parse(value)));
                break;
            case DECIMAL:
                result = new SqlParameterValue(at.getSqlType(), Double.valueOf(value));
                break;
            case INTEGER:
                result = new SqlParameterValue(at.getSqlType(), Integer.valueOf(value));
                break;
            case VARCHAR:
                result = new SqlParameterValue(at.getSqlType(), getWildcardedValue(op, value));
                break;
            case TEXT:
                result = new SqlParameterValue(at.getSqlType(), getWildcardedValue(op, value));
                break;
            case JSON_BLOB:
                result = new SqlParameterValue(at.getSqlType(), getWildcardedValue(op, value));
                break;
        }
        return result;
    }
    
    protected String getWildcardedValue(Operator op, String value) {
        String result = null;
        switch (op) {
            case STARTS_WITH:
                result = String.format("%s%s", value, "%");
                break;
            case ENDS_WITH:
                result = String.format("%s%s", "%", value);
                break;
            case CONTAINS:
                result = String.format("%s%s%s", "%", value, "%");
                break;
            default:
                result = value;
                break;
        }
        return result;
    }
    
}
