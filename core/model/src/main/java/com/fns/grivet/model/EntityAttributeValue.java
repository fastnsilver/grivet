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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * An {@code EntityAttributeValue} is the reification of a {@code ClassAttribute} plus a value.
 *
 * @author Chris Phillipson
 */
@RequiredArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode
public class EntityAttributeValue implements Serializable {

    /**
     * Version number used during deserialization to verify that the sender and receiver
     * of this serialized object have loaded classes for this object that
     * are compatible with respect to serialization.
     */
    private static final long serialVersionUID = 1L;

    /** An entity identifier */
    private final Long id;
    /** An {@code Attribute} identifier */
    private final Integer attributeId;
    /** An {@code Attribute} name */
    private final String attributeName;
    /** A value */
    private final Object attributeValue;
    /** The time this {@code EntityAttributeValue} was created */
    private final LocalDateTime createdTime;

    private final String createdBy;

}
