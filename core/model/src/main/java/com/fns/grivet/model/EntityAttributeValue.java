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

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * An {@code EntityAttributeValue} is the reification of a {@code ClassAttribute} plus a value.
 *
 * @author Chris Phillipson
 */
public class EntityAttributeValue implements Serializable {

    /**
     * Version number used during deserialization to verify that the sender and receiver
     * of this serialized object have loaded classes for this object that
     * are compatible with respect to serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * An entity identifier
     */
    private final Long id;

    /**
     * An {@code Attribute} identifier
     */
    private final Integer attributeId;

    /**
     * An {@code Attribute} name
     */
    private final String attributeName;

    /**
     * A value
     */
    private final Object attributeValue;

    /**
     * The time this {@code EntityAttributeValue} was created
     */
    private final LocalDateTime createdTime;

    private final String createdBy;

    private EntityAttributeValue(final Long id, final Integer attributeId, final String attributeName, final Object attributeValue, final LocalDateTime createdTime, final String createdBy) {
        this.id = id;
        this.attributeId = attributeId;
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.createdTime = createdTime;
        this.createdBy = createdBy;
    }

    public static EntityAttributeValue of(final Long id, final Integer attributeId, final String attributeName, final Object attributeValue, final LocalDateTime createdTime, final String createdBy) {
        return new EntityAttributeValue(id, attributeId, attributeName, attributeValue, createdTime, createdBy);
    }

    /**
     * An entity identifier
     */
    public Long getId() {
        return this.id;
    }

    /**
     * An {@code Attribute} identifier
     */
    public Integer getAttributeId() {
        return this.attributeId;
    }

    /**
     * An {@code Attribute} name
     */
    public String getAttributeName() {
        return this.attributeName;
    }

    /**
     * A value
     */
    public Object getAttributeValue() {
        return this.attributeValue;
    }

    /**
     * The time this {@code EntityAttributeValue} was created
     */
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof EntityAttributeValue)) return false;
        final EntityAttributeValue other = (EntityAttributeValue) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$attributeId = this.getAttributeId();
        final Object other$attributeId = other.getAttributeId();
        if (this$attributeId == null ? other$attributeId != null : !this$attributeId.equals(other$attributeId)) return false;
        final Object this$attributeName = this.getAttributeName();
        final Object other$attributeName = other.getAttributeName();
        if (this$attributeName == null ? other$attributeName != null : !this$attributeName.equals(other$attributeName)) return false;
        final Object this$attributeValue = this.getAttributeValue();
        final Object other$attributeValue = other.getAttributeValue();
        if (this$attributeValue == null ? other$attributeValue != null : !this$attributeValue.equals(other$attributeValue)) return false;
        final Object this$createdTime = this.getCreatedTime();
        final Object other$createdTime = other.getCreatedTime();
        if (this$createdTime == null ? other$createdTime != null : !this$createdTime.equals(other$createdTime)) return false;
        final Object this$createdBy = this.getCreatedBy();
        final Object other$createdBy = other.getCreatedBy();
        if (this$createdBy == null ? other$createdBy != null : !this$createdBy.equals(other$createdBy)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EntityAttributeValue;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $attributeId = this.getAttributeId();
        result = result * PRIME + ($attributeId == null ? 43 : $attributeId.hashCode());
        final Object $attributeName = this.getAttributeName();
        result = result * PRIME + ($attributeName == null ? 43 : $attributeName.hashCode());
        final Object $attributeValue = this.getAttributeValue();
        result = result * PRIME + ($attributeValue == null ? 43 : $attributeValue.hashCode());
        final Object $createdTime = this.getCreatedTime();
        result = result * PRIME + ($createdTime == null ? 43 : $createdTime.hashCode());
        final Object $createdBy = this.getCreatedBy();
        result = result * PRIME + ($createdBy == null ? 43 : $createdBy.hashCode());
        return result;
    }
}
