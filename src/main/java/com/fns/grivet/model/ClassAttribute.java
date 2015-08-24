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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fns.grivet.query.NamedQuery;

/**
 * A {@code ClassAttribute} expresses a three-way relationship between a
 * {@link Class#id}, an {@link Attribute#id} and an {@link AttributeType#id}. A
 * {@code Class} typically has one or more {@code ClassAttribute}.
 * 
 * @author Chris Phillipson
 */
@Entity
@IdClass(ClassAttributePK.class)
public class ClassAttribute extends Audited {

    /**
     * Version number used during deserialization to verify that the sender and
     * receiver of this serialized object have loaded classes for this object
     * that are compatible with respect to serialization.
     */
    private static final long serialVersionUID = 1L;

    /** A {@code Class} identifier */
    @Id
    private Integer cid;

    /** A {@code Attribute} identifier */
    @Id
    private Integer aid;

    /** A {@code AttributeType} identifier */
    @Id
    private Integer tid;

    protected ClassAttribute() {
        super();
    }

    public ClassAttribute(Integer cid, Integer aid, Integer tid, User user) {
        super(user);
        this.cid = cid;
        this.aid = aid;
        this.tid = tid;
    }

    public Integer getCid() {
        return cid;
    }

    public Integer getAid() {
        return aid;
    }

    public Integer getTid() {
        return tid;
    }
    
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(cid).append(aid).append(tid).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NamedQuery) == false) {
            return false;
        }
        ClassAttribute rhs = ((ClassAttribute) other);
        return new EqualsBuilder().appendSuper(true).append(cid, rhs.cid).append(aid, rhs.aid)
                .append(tid, rhs.tid).isEquals();
    }

}
