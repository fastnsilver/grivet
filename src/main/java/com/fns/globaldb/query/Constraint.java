package com.fns.globaldb.query;

import org.springframework.util.Assert;

public class Constraint {

    private final String attributeName;
    private final Operator operator;
    private final Conjunction conjunction;
    private final String[] values;
    
    public Constraint(String attributeName, Operator operator, Conjunction conjunction, String[] values) {
        this.attributeName = attributeName;
        this.operator = operator;
        this.conjunction = conjunction;
        this.values = values;
    }

    // c=<attributeName>|<operator>|<value>|<conjunction>
    public Constraint(String[] constraintParts) {
        attributeName = constraintParts[0];
        operator = Operator.fromValue(constraintParts[1]);
        values = constraintParts[2].split("\\s*,\\s*");
        if (operator.equals(Operator.BETWEEN)) {
            Assert.isTrue(values.length == 2, "Operator [between] requires two values!");
        } 
        if (constraintParts.length == 4) {
            if (constraintParts[3] == null) {
                conjunction = null;
            } else {
                conjunction = Conjunction.fromValue(constraintParts[3]);
            }
        } else {
            conjunction = null;
        }
    }

    public String getAttributeName() {
        return attributeName;
    }
    public Operator getOperator() {
        return operator;
    }

    public Conjunction getConjunction() {
        return conjunction;
    }
    
    public String[] getValues() {
        return values;
    }
    
}
