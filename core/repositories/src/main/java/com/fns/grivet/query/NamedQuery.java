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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fns.grivet.model.AttributeType;
import com.fns.grivet.model.Auditable;

import lombok.Builder;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "name",
    "type",
    "query",
    "params"
})
@Data
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class NamedQuery implements Auditable<String> {

    /** 
     * Version number used during deserialization to verify that the sender and receiver 
     * of this serialized object have loaded classes for this object that 
     * are compatible with respect to serialization. 
     */
    private static final long serialVersionUID = 1L;
    
    @Column
    @CreatedBy
    private String createdBy;
    
    @Column
    @LastModifiedBy
    private String updatedBy;
    
    /** The time this entity was created. */
    @Column(nullable=false, updatable = false)
    @Convert(disableConversion = true)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @CreatedDate
    private LocalDateTime createdTime;
    
    /** The time this entity was last modified. */
    @Column
    @Convert(disableConversion = true)
    @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
    @LastModifiedDate
    private LocalDateTime updatedTime;
    
    @Version
    @Column
    private long version;
    
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @JsonProperty("name")
    @Size(max=255)
    @Column(nullable=false, unique=true)
    private String name;
    
    
    @Size(max=2000)
    @Column(length=2000, nullable=false)
    private String query;
    
    @JsonProperty("type")
    @Enumerated(EnumType.STRING)
    private QueryType type;
    
    
    @Valid
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="named_query_parameter", joinColumns=@JoinColumn(name="id"))
    @MapKeyColumn(name="parameter_name")
    @Column(name="parameter_type", nullable=false)
    private Map<String, String> params;
        
    NamedQuery() {
        setParams(null);
    }
    
    NamedQuery(String createdBy, String updatedBy, LocalDateTime createdTime, LocalDateTime updatedTime,
            long version, Integer id, String name, String query, QueryType type, Map<String, String> params) {
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.version = version;
        this.id = id;
        this.name = name;
        setQuery(query);
        this.type = type;
        setParams(params);
    }
    
    @JsonProperty("query")
    public void setQuery(String query) {
       this.query = query;
       if (StringUtils.hasText(query)) {
           if (query.toUpperCase().startsWith("SELECT")) {
               this.type = QueryType.SELECT;
           }
           if (query.toUpperCase().startsWith("CALL")) {
               this.type = QueryType.SPROC;
           }
       } 
    }
    
    @JsonProperty("params")
    public void setParams(Map<String, String> params) {
        this.params = params;
        if (params == null) {
            this.params = new HashMap<>();
        }
    }
    
    // only when parameter values are not null, empty, or blank 
    // and parameter definition for named query is not empty
    // will a MapSqlParameterSource be constructed
    public MapSqlParameterSource asParameterSource(MultiValueMap<String, ?> parameterValues) {
        MapSqlParameterSource paramSource = null;
        if (!CollectionUtils.isEmpty(parameterValues) && !getParams().isEmpty()) {
            paramSource = new MapSqlParameterSource();
            Set<Entry<String, String>> paramNameToParamTypeEntries = getParams().entrySet();
            List<?> values = null; 
            for (Entry<String, String> entry: paramNameToParamTypeEntries) {
                values = parameterValues.get(entry.getKey());
                if (!CollectionUtils.isEmpty(values)) {
                    if (values.size() == 1) {
                        paramSource.addValue(entry.getKey(), parameterValues.getFirst(entry.getKey()), AttributeType.toSqlType(entry.getValue()));
                    } else {
                        paramSource.addValue(entry.getKey(), values);
                    }
                }
            }
        }
        return paramSource;
    }
    
    // only when parameter values are not null, empty, or blank 
    // and parameter definition for named query is not empty
    // will a List<SqlParameter> be constructed
    public List<SqlParameter> asSqlParameters(MultiValueMap<String, ?> parameterValues) {
        List<SqlParameter> sqlParams = null;
        if (!CollectionUtils.isEmpty(parameterValues) && !getParams().isEmpty()) {
            sqlParams = new ArrayList<>();
            Set<Entry<String, String>> paramNameToParamTypeEntries = getParams().entrySet();
            List<?> values = null; 
            for (Entry<String, String> entry: paramNameToParamTypeEntries) {
                values = parameterValues.get(entry.getKey());
                if (!CollectionUtils.isEmpty(values)) {
                    if (values.size() == 1) {
                        sqlParams.add(new SqlParameter(entry.getKey(), AttributeType.toSqlType(entry.getValue())));
                    } else {
                        throw new IllegalArgumentException(String.format("Stored Procedure cannot be executed! Parameter [%s] is not a scalar value!", entry.getKey()));
                    }
                }
            }
        }
        return sqlParams;
    }

}
