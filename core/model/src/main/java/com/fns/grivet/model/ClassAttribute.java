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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A {@code ClassAttribute} expresses a three-way relationship between a
 * {@link Class#id}, an {@link Attribute#id} and an {@link AttributeType#id}. A
 * {@code Class} typically has one or more {@code ClassAttribute}.
 *
 * @author Chris Phillipson
 */
@Data
@Builder
@NoArgsConstructor(access=AccessLevel.PACKAGE)
@AllArgsConstructor(access=AccessLevel.PACKAGE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@IdClass(ClassAttributePK.class)
public class ClassAttribute implements Auditable<String> {

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
    @Column(nullable=false, updatable = false, columnDefinition = "TIMESTAMP")
    @CreatedDate
    private LocalDateTime createdTime;

    /** The time this entity was last modified. */
    @Column(columnDefinition = "TIMESTAMP")
    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Version
    @Column
    private long version;

    /** A {@code Class} identifier */
    @Id
    private Integer cid;

    /** A {@code Attribute} identifier */
    @Id
    private Integer aid;

    /** A {@code AttributeType} identifier */
    @Id
    private Integer tid;

}
