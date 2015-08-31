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
 * Type-safe enumerator for comparison and logic based operators.
 * 
 * @author Chris Phillipson
 */
public enum Operator {

    STARTS_WITH("startsWith", "LIKE"),
    ENDS_WITH("endsWith", "LIKE"),
    CONTAINS("contains", "LIKE"),
    EQUALS("equals", "="),
    NOT_EQUAL_TO("notEqualTo", "<>"),
    GREATER_THAN("greaterThan", ">"),
    LESS_THAN("lessThan", "<"),
    GREATER_THAN_OR_EQUAL_TO("greaterThanOrEqualTo", ">="),
    LESS_THAN_OR_EQUAL_TO("lessThanOrEqualTo", "<="),
    BETWEEN("between", "BETWEEN"),
    IN("in", "IN");
    
    private String name;
    private String symbol;
    
    Operator(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public static Operator fromValue(String value) {
        List<Operator> ops = Arrays.stream(Operator.values()).filter(o -> o.getName().equalsIgnoreCase(value)).collect(Collectors.toList());
        Assert.notEmpty(ops, String.format("Invalid Operator [%s]", value));
        return ops.get(0);
    }
    
}
