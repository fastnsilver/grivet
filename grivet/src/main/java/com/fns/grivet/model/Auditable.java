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
 * Interface for {@code Auditable} entities. Allows storing and retrieving creation and update information. The changing
 * instance (typically some user) is to be defined by a Generics definition.
 * 
 * @param <U> the auditing type. Typically some kind of user; or reference to user's identifier
 *
 * @author Chris Phillipson
 */
public interface Auditable<U> extends Serializable {
    
    /**
     * Returns the user who created this entity.
     * 
     * @return a user
     */
    U getCreatedBy();

    /**
     * Sets the user who created this entity.
     * 
     * @param createdBy a user
     */
    void setCreatedBy(final U createdBy);

    /**
     * Returns the creation time of the entity.
     * 
     * @return the createdTime; 
     *   a date-time without a time-zone in the ISO-8601 calendar system
     */
    LocalDateTime getCreatedTime();

    /**
     * Sets the creation time of the entity.
     * 
     * @param createdTime the creation date to set; 
     *   a date-time without a time-zone in the ISO-8601 calendar system
     */
    void setCreatedTime(final LocalDateTime createdTime);

    /**
     * Returns the user who last modified the entity.
     * 
     * @return a user
     */
    U getUpdatedBy();

    /**
     * Sets the user who last modified the entity.
     * 
     * @param updatedBy a user
     */
    void setUpdatedBy(final U updatedBy);

    /**
     * Returns the time of the last modification.
     * 
     * @return the last updated time; 
     *   a date-time without a time-zone in the ISO-8601 calendar system
     */
    LocalDateTime getUpdatedTime();

    /**
     * Sets the time of the last modification.
     * 
     * @param updatedTime the time of the last modification;
     *   a date-time without a time-zone in the ISO-8601 calendar system
     */
    void setUpdatedTime(final LocalDateTime updatedTime);

}
