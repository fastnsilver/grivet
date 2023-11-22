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
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "name", "type", "query", "params" })
@Entity
@EntityListeners(AuditingEntityListener.class)
public class NamedQuery implements Auditable<String> {

	/**
	 * Version number used during deserialization to verify that the sender and receiver
	 * of this serialized object have loaded classes for this object that are compatible
	 * with respect to serialization.
	 */
	private static final long serialVersionUID = 1L;

	@Column
	@CreatedBy
	private String createdBy;

	@Column
	@LastModifiedBy
	private String updatedBy;

	/**
	 * The time this entity was created.
	 */
	@Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
	@CreatedDate
	private LocalDateTime createdTime;

	/**
	 * The time this entity was last modified.
	 */
	@Column(columnDefinition = "TIMESTAMP")
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
	@Size(max = 255)
	@Column(nullable = false, unique = true)
	private String name;

	@Size(max = 2000)
	@Column(length = 2000, nullable = false)
	private String query;

	@JsonProperty("type")
	@Enumerated(EnumType.STRING)
	private QueryType type;

	@Valid
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "named_query_parameter", joinColumns = @JoinColumn(name = "id"))
	@MapKeyColumn(name = "parameter_name")
	@Column(name = "parameter_type", nullable = false)
	private Map<String, String> params;

	NamedQuery() {
		setParams(null);
	}

	NamedQuery(String createdBy, String updatedBy, LocalDateTime createdTime, LocalDateTime updatedTime, long version,
			Integer id, String name, String query, QueryType type, Map<String, String> params) {
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
			for (Entry<String, String> entry : paramNameToParamTypeEntries) {
				values = parameterValues.get(entry.getKey());
				if (!CollectionUtils.isEmpty(values)) {
					if (values.size() == 1) {
						paramSource.addValue(entry.getKey(), parameterValues.getFirst(entry.getKey()),
								AttributeType.toSqlType(entry.getValue()));
					}
					else {
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
			for (Entry<String, String> entry : paramNameToParamTypeEntries) {
				values = parameterValues.get(entry.getKey());
				if (!CollectionUtils.isEmpty(values)) {
					if (values.size() == 1) {
						sqlParams.add(new SqlParameter(entry.getKey(), AttributeType.toSqlType(entry.getValue())));
					}
					else {
						throw new IllegalArgumentException(
								"Stored Procedure cannot be executed! Parameter [%s] is not a scalar value!"
									.formatted(entry.getKey()));
					}
				}
			}
		}
		return sqlParams;
	}

	public static class NamedQueryBuilder {

		private String createdBy;

		private String updatedBy;

		private LocalDateTime createdTime;

		private LocalDateTime updatedTime;

		private long version;

		private Integer id;

		private String name;

		private String query;

		private QueryType type;

		private Map<String, String> params;

		NamedQueryBuilder() {
		}

		/**
		 * @return {@code this}.
		 */
		public NamedQuery.NamedQueryBuilder createdBy(final String createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		public NamedQuery.NamedQueryBuilder updatedBy(final String updatedBy) {
			this.updatedBy = updatedBy;
			return this;
		}

		/**
		 * The time this entity was created.
		 * @return {@code this}.
		 */
		public NamedQuery.NamedQueryBuilder createdTime(final LocalDateTime createdTime) {
			this.createdTime = createdTime;
			return this;
		}

		/**
		 * The time this entity was last modified.
		 * @return {@code this}.
		 */
		public NamedQuery.NamedQueryBuilder updatedTime(final LocalDateTime updatedTime) {
			this.updatedTime = updatedTime;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		public NamedQuery.NamedQueryBuilder version(final long version) {
			this.version = version;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@JsonIgnore
		public NamedQuery.NamedQueryBuilder id(final Integer id) {
			this.id = id;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@JsonProperty("name")
		public NamedQuery.NamedQueryBuilder name(final String name) {
			this.name = name;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		public NamedQuery.NamedQueryBuilder query(final String query) {
			this.query = query;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		@JsonProperty("type")
		public NamedQuery.NamedQueryBuilder type(final QueryType type) {
			this.type = type;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		public NamedQuery.NamedQueryBuilder params(final Map<String, String> params) {
			this.params = params;
			return this;
		}

		public NamedQuery build() {
			return new NamedQuery(this.createdBy, this.updatedBy, this.createdTime, this.updatedTime, this.version,
					this.id, this.name, this.query, this.type, this.params);
		}

		@Override
		public String toString() {
			return "NamedQuery.NamedQueryBuilder(createdBy=" + this.createdBy + ", updatedBy=" + this.updatedBy
					+ ", createdTime=" + this.createdTime + ", updatedTime=" + this.updatedTime + ", version="
					+ this.version + ", id=" + this.id + ", name=" + this.name + ", query=" + this.query + ", type="
					+ this.type + ", params=" + this.params + ")";
		}

	}

	public static NamedQuery.NamedQueryBuilder builder() {
		return new NamedQuery.NamedQueryBuilder();
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public String getUpdatedBy() {
		return this.updatedBy;
	}

	/**
	 * The time this entity was created.
	 */
	public LocalDateTime getCreatedTime() {
		return this.createdTime;
	}

	/**
	 * The time this entity was last modified.
	 */
	public LocalDateTime getUpdatedTime() {
		return this.updatedTime;
	}

	public long getVersion() {
		return this.version;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getQuery() {
		return this.query;
	}

	public QueryType getType() {
		return this.type;
	}

	public Map<String, String> getParams() {
		return this.params;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public void setUpdatedBy(final String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * The time this entity was created.
	 */
	public void setCreatedTime(final LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * The time this entity was last modified.
	 */
	public void setUpdatedTime(final LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	public void setVersion(final long version) {
		this.version = version;
	}

	@JsonIgnore
	public void setId(final Integer id) {
		this.id = id;
	}

	@JsonProperty("name")
	public void setName(final String name) {
		this.name = name;
	}

	@JsonProperty("type")
	public void setType(final QueryType type) {
		this.type = type;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this)
			return true;
		if (!(o instanceof NamedQuery))
			return false;
		final NamedQuery other = (NamedQuery) o;
		if (!other.canEqual((Object) this))
			return false;
		if (this.getVersion() != other.getVersion())
			return false;
		final var this$id = this.getId();
		final var other$id = other.getId();
		if (this$id == null ? other$id != null : !this$id.equals(other$id))
			return false;
		final var this$createdBy = this.getCreatedBy();
		final var other$createdBy = other.getCreatedBy();
		if (this$createdBy == null ? other$createdBy != null : !this$createdBy.equals(other$createdBy))
			return false;
		final var this$updatedBy = this.getUpdatedBy();
		final var other$updatedBy = other.getUpdatedBy();
		if (this$updatedBy == null ? other$updatedBy != null : !this$updatedBy.equals(other$updatedBy))
			return false;
		final var this$createdTime = this.getCreatedTime();
		final var other$createdTime = other.getCreatedTime();
		if (this$createdTime == null ? other$createdTime != null : !this$createdTime.equals(other$createdTime))
			return false;
		final var this$updatedTime = this.getUpdatedTime();
		final var other$updatedTime = other.getUpdatedTime();
		if (this$updatedTime == null ? other$updatedTime != null : !this$updatedTime.equals(other$updatedTime))
			return false;
		final var this$name = this.getName();
		final var other$name = other.getName();
		if (this$name == null ? other$name != null : !this$name.equals(other$name))
			return false;
		final var this$query = this.getQuery();
		final var other$query = other.getQuery();
		if (this$query == null ? other$query != null : !this$query.equals(other$query))
			return false;
		final Object this$type = this.getType();
		final Object other$type = other.getType();
		if (this$type == null ? other$type != null : !this$type.equals(other$type))
			return false;
		final var this$params = this.getParams();
		final var other$params = other.getParams();
		if (this$params == null ? other$params != null : !this$params.equals(other$params))
			return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof NamedQuery;
	}

	@Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final long $version = this.getVersion();
		result = result * PRIME + (int) ($version >>> 32 ^ $version);
		final var $id = this.getId();
		result = result * PRIME + ($id == null ? 43 : $id.hashCode());
		final var $createdBy = this.getCreatedBy();
		result = result * PRIME + ($createdBy == null ? 43 : $createdBy.hashCode());
		final var $updatedBy = this.getUpdatedBy();
		result = result * PRIME + ($updatedBy == null ? 43 : $updatedBy.hashCode());
		final var $createdTime = this.getCreatedTime();
		result = result * PRIME + ($createdTime == null ? 43 : $createdTime.hashCode());
		final var $updatedTime = this.getUpdatedTime();
		result = result * PRIME + ($updatedTime == null ? 43 : $updatedTime.hashCode());
		final var $name = this.getName();
		result = result * PRIME + ($name == null ? 43 : $name.hashCode());
		final var $query = this.getQuery();
		result = result * PRIME + ($query == null ? 43 : $query.hashCode());
		final Object $type = this.getType();
		result = result * PRIME + ($type == null ? 43 : $type.hashCode());
		final var $params = this.getParams();
		result = result * PRIME + ($params == null ? 43 : $params.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "NamedQuery(createdBy=" + this.getCreatedBy() + ", updatedBy=" + this.getUpdatedBy() + ", createdTime="
				+ this.getCreatedTime() + ", updatedTime=" + this.getUpdatedTime() + ", version=" + this.getVersion()
				+ ", id=" + this.getId() + ", name=" + this.getName() + ", query=" + this.getQuery() + ", type="
				+ this.getType() + ", params=" + this.getParams() + ")";
	}

}
