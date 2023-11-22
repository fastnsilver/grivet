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
package com.fns.grivet.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Version;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * A {@code ClassAttribute} expresses a three-way relationship between a {@link Class#id},
 * an {@link Attribute#id} and an {@link AttributeType#id}. A {@code Class} typically has
 * one or more {@code ClassAttribute}.
 *
 * @author Chris Phillipson
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@IdClass(ClassAttributePK.class)
public class ClassAttribute implements Auditable<String> {

	/**
	 * Version number used during deserialization to verify that the sender and receiver
	 * of this serialized object have loaded classes for this object that are compatible
	 * with respect to serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The user who created this entity.
	 */
	@Column
	@CreatedBy
	private String createdBy;

	/**
	 * The user who last modified this entity.
	 */
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

	/**
	 * The optimistic locking mechanism used to version the entity.
	 */
	@Version
	@Column
	private long version;

	/**
	 * A {@code Class} identifier
	 */
	@Id
	private Integer cid;

	/**
	 * A {@code Attribute} identifier
	 */
	@Id
	private Integer aid;

	/**
	 * A {@code AttributeType} identifier
	 */
	@Id
	private Integer tid;

	/**
	 * {@code ClassAttribute} builder static inner class.
	 */
	public static class ClassAttributeBuilder {

		private String createdBy;

		private String updatedBy;

		private LocalDateTime createdTime;

		private LocalDateTime updatedTime;

		private long version;

		private Integer cid;

		private Integer aid;

		private Integer tid;

		ClassAttributeBuilder() {
		}

		/**
		 * @return {@code this}.
		 */
		public ClassAttribute.ClassAttributeBuilder createdBy(final String createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		public ClassAttribute.ClassAttributeBuilder updatedBy(final String updatedBy) {
			this.updatedBy = updatedBy;
			return this;
		}

		/**
		 * The time this entity was created.
		 * @return {@code this}.
		 */
		public ClassAttribute.ClassAttributeBuilder createdTime(final LocalDateTime createdTime) {
			this.createdTime = createdTime;
			return this;
		}

		/**
		 * The time this entity was last modified.
		 * @return {@code this}.
		 */
		public ClassAttribute.ClassAttributeBuilder updatedTime(final LocalDateTime updatedTime) {
			this.updatedTime = updatedTime;
			return this;
		}

		/**
		 * @return {@code this}.
		 */
		public ClassAttribute.ClassAttributeBuilder version(final long version) {
			this.version = version;
			return this;
		}

		/**
		 * A {@code Class} identifier
		 * @return {@code this}.
		 */
		public ClassAttribute.ClassAttributeBuilder cid(final Integer cid) {
			this.cid = cid;
			return this;
		}

		/**
		 * A {@code Attribute} identifier
		 * @return {@code this}.
		 */
		public ClassAttribute.ClassAttributeBuilder aid(final Integer aid) {
			this.aid = aid;
			return this;
		}

		/**
		 * A {@code AttributeType} identifier
		 * @return {@code this}.
		 */
		public ClassAttribute.ClassAttributeBuilder tid(final Integer tid) {
			this.tid = tid;
			return this;
		}

		public ClassAttribute build() {
			return new ClassAttribute(this.createdBy, this.updatedBy, this.createdTime, this.updatedTime, this.version,
					this.cid, this.aid, this.tid);
		}

		@Override
		public String toString() {
			return "ClassAttribute.ClassAttributeBuilder(createdBy=" + this.createdBy + ", updatedBy=" + this.updatedBy
					+ ", createdTime=" + this.createdTime + ", updatedTime=" + this.updatedTime + ", version="
					+ this.version + ", cid=" + this.cid + ", aid=" + this.aid + ", tid=" + this.tid + ")";
		}

	}

	public static ClassAttribute.ClassAttributeBuilder builder() {
		return new ClassAttribute.ClassAttributeBuilder();
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

	/**
	 * A {@code Class} identifier
	 */
	public Integer getCid() {
		return this.cid;
	}

	/**
	 * A {@code Attribute} identifier
	 */
	public Integer getAid() {
		return this.aid;
	}

	/**
	 * A {@code AttributeType} identifier
	 */
	public Integer getTid() {
		return this.tid;
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

	/**
	 * A {@code Class} identifier
	 */
	public void setCid(final Integer cid) {
		this.cid = cid;
	}

	/**
	 * A {@code Attribute} identifier
	 */
	public void setAid(final Integer aid) {
		this.aid = aid;
	}

	/**
	 * A {@code AttributeType} identifier
	 */
	public void setTid(final Integer tid) {
		this.tid = tid;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ClassAttribute))
			return false;
		final ClassAttribute other = (ClassAttribute) o;
		if (!other.canEqual((Object) this))
			return false;
		if (this.getVersion() != other.getVersion())
			return false;
		final Object this$cid = this.getCid();
		final Object other$cid = other.getCid();
		if (this$cid == null ? other$cid != null : !this$cid.equals(other$cid))
			return false;
		final Object this$aid = this.getAid();
		final Object other$aid = other.getAid();
		if (this$aid == null ? other$aid != null : !this$aid.equals(other$aid))
			return false;
		final Object this$tid = this.getTid();
		final Object other$tid = other.getTid();
		if (this$tid == null ? other$tid != null : !this$tid.equals(other$tid))
			return false;
		final Object this$createdBy = this.getCreatedBy();
		final Object other$createdBy = other.getCreatedBy();
		if (this$createdBy == null ? other$createdBy != null : !this$createdBy.equals(other$createdBy))
			return false;
		final Object this$updatedBy = this.getUpdatedBy();
		final Object other$updatedBy = other.getUpdatedBy();
		if (this$updatedBy == null ? other$updatedBy != null : !this$updatedBy.equals(other$updatedBy))
			return false;
		final Object this$createdTime = this.getCreatedTime();
		final Object other$createdTime = other.getCreatedTime();
		if (this$createdTime == null ? other$createdTime != null : !this$createdTime.equals(other$createdTime))
			return false;
		final Object this$updatedTime = this.getUpdatedTime();
		final Object other$updatedTime = other.getUpdatedTime();
		if (this$updatedTime == null ? other$updatedTime != null : !this$updatedTime.equals(other$updatedTime))
			return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof ClassAttribute;
	}

	@Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final long $version = this.getVersion();
		result = result * PRIME + (int) ($version >>> 32 ^ $version);
		final Object $cid = this.getCid();
		result = result * PRIME + ($cid == null ? 43 : $cid.hashCode());
		final Object $aid = this.getAid();
		result = result * PRIME + ($aid == null ? 43 : $aid.hashCode());
		final Object $tid = this.getTid();
		result = result * PRIME + ($tid == null ? 43 : $tid.hashCode());
		final Object $createdBy = this.getCreatedBy();
		result = result * PRIME + ($createdBy == null ? 43 : $createdBy.hashCode());
		final Object $updatedBy = this.getUpdatedBy();
		result = result * PRIME + ($updatedBy == null ? 43 : $updatedBy.hashCode());
		final Object $createdTime = this.getCreatedTime();
		result = result * PRIME + ($createdTime == null ? 43 : $createdTime.hashCode());
		final Object $updatedTime = this.getUpdatedTime();
		result = result * PRIME + ($updatedTime == null ? 43 : $updatedTime.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "ClassAttribute(createdBy=" + this.getCreatedBy() + ", updatedBy=" + this.getUpdatedBy()
				+ ", createdTime=" + this.getCreatedTime() + ", updatedTime=" + this.getUpdatedTime() + ", version="
				+ this.getVersion() + ", cid=" + this.getCid() + ", aid=" + this.getAid() + ", tid=" + this.getTid()
				+ ")";
	}

	ClassAttribute() {
	}

	ClassAttribute(final String createdBy, final String updatedBy, final LocalDateTime createdTime,
			final LocalDateTime updatedTime, final long version, final Integer cid, final Integer aid,
			final Integer tid) {
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.createdTime = createdTime;
		this.updatedTime = updatedTime;
		this.version = version;
		this.cid = cid;
		this.aid = aid;
		this.tid = tid;
	}

}
