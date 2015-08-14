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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * A {@code ClassAttribute} expresses a three-way relationship between a {@link Class#id}, an
 * {@link Attribute#id} and an {@link AttributeType#id}.  A {@code Class} typically has one or more
 * {@code ClassAttribute}.
 * 
 * @author Chris Phillipson
 */
@Entity
@IdClass(ClassAttributePK.class)
public class ClassAttribute implements Serializable {

    /** 
     * Version number used during deserialization to verify that the sender and receiver 
     * of this serialized object have loaded classes for this object that 
     * are compatible with respect to serialization. 
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
    
    /** The time this {@code ClassAttribute} was created */
    @Column(nullable=false)
    private LocalDateTime createdTime;

    protected ClassAttribute() {
        // no-args constructor required by JPA spec
    }
    
    public ClassAttribute(Integer cid, Integer aid, Integer tid) {
        this.cid = cid;
        this.aid = aid;
        this.tid = tid;
        this.createdTime = LocalDateTime.now();
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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

}
