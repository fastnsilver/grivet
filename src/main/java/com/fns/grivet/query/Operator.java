package com.fns.grivet.query;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;


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
