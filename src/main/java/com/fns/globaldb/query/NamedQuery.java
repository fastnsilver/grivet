
package com.fns.globaldb.query;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fns.globaldb.model.AttributeType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "name",
    "type",
    "query",
    "params"
})
@Entity
public class NamedQuery implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @JsonIgnore
    @Id
    @GeneratedValue
    private Integer id;
    
    @JsonProperty("name")
    @Size(max=255)
    @Column(nullable=false, unique=true)
    private String name;
    
    @JsonProperty("type")
    @Enumerated(EnumType.STRING)
    private QueryType type;
    
    @JsonProperty("query")
    @Size(max=2000)
    @Column(length=2000, nullable=false)
    private String query;
    
    @JsonProperty("params")
    @Valid
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="named_query_parameter", joinColumns=@JoinColumn(name="id"))
    @MapKeyColumn(name="parameter_name")
    @Column(name="parameter_type", nullable=false)
    private Map<String, String> params;
    
    @Column(nullable=false)
    private LocalDateTime createdTime;

    protected NamedQuery() {
        // no-args constructor required by JPA spec
        // this one is protected since it shouldn't be used directly
    }
    
    @JsonCreator
    public NamedQuery(@JsonProperty("name") String name, @JsonProperty("type") QueryType type, @JsonProperty("query") String query, @JsonProperty("params") Map<String, String> params) {
        this.name = name;
        this.query = query;
        setType(type);
        this.params = params;
        this.createdTime = LocalDateTime.now();
    }
    
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    private void setType(QueryType type) {
       if (type == null) {
           if (query.toUpperCase().startsWith("SELECT")) {
               this.type = QueryType.SELECT;
           }
           if (query.toUpperCase().startsWith("CALL")) {
               this.type = QueryType.SPROC;
           }
       } else {
           this.type = type;
       }
    }
    
    public QueryType getType() {
        return type;
    }

    public String getQuery() {
        return query;
    }

    public Map<String, String> getParams() {
        if (params == null) {
            params = new HashMap<String, String>();
        }
        return params;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
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
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(name).append(type).append(query).append(params).append(createdTime).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NamedQuery) == false) {
            return false;
        }
        NamedQuery rhs = ((NamedQuery) other);
        return new EqualsBuilder().append(id, rhs.id).append(name, rhs.name).append(type, rhs.type).append(query, rhs.query).append(params, rhs.params).append(createdTime, rhs.createdTime).isEquals();
    }

}
