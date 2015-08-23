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

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Audited implements Auditable<Integer>{

    @Column
    private Integer createdBy;
    
    @Column
    private Integer updatedBy;
    
    /** The time this entity was created. */
    @Column(nullable=false)
    private LocalDateTime createdTime;
    
    /** The time this entity was last modified. */
    @Column
    private LocalDateTime updatedTime;
    
    /** 
     * Version number used during deserialization to verify that the sender and receiver 
     * of this serialized object have loaded classes for this object that 
     * are compatible with respect to serialization. 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new entity.
     */
    protected Audited() {
        this.updatedTime = LocalDateTime.now();
        this.createdTime = updatedTime;
    }
    
    @Override
    public Integer getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public Integer getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
}
