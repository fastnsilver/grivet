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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;


/**
 * A logical operator used to combine two or more query conditions.
 * @see Constraint
 * 
 * @author Chris Phillipson
 */
public enum Conjunction {

    AND("AND"),
    OR("OR");
    
    /** A logical operator name. */
    private String name;
    
    /**
     * Instantiates a new conjunction.
     *
     * @param name
     *            the logical operator name
     */
    Conjunction(String name) {
        this.name = name;
    }
    
    /**
     * Gets the logical operator name.
     *
     * @return the logical operator name
     */
    public String getName() {
        return name;
    }
    
    /**
     * From name.
     *
     * @param name
     *            a logical operator name
     * @return the conjunction
     * @throws IllegalArgumentException when name does not match any internal {@link Conjunction#name}
     */
    public static Conjunction fromValue(String name) {
        List<Conjunction> conj = Arrays.stream(Conjunction.values()).filter(o -> o.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
        Assert.notEmpty(conj, "Invalid Conjunction [%s]".formatted(name));
        return conj.get(0);
    }
}
