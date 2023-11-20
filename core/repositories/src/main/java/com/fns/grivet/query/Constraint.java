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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;

public class Constraint {

    private final String attributeName;
    private final Operator operator;
    private final Conjunction conjunction;
    private final String[] values;

    // c=<attributeName>|<operator>|<value>|<conjunction>
    public Constraint(String[] constraintParts) {
        Assert.notEmpty(constraintParts, "Constraint parts must not be null or empty!");
        Assert.isTrue(constraintParts.length >= 3, "Must have 3 or more constraint parts!");
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(attributeName).append(operator).append(conjunction).append(values).toHashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Constraint)) {
            return false;
        }
        Constraint that = (Constraint) object;
        return new EqualsBuilder().append(this.attributeName, that.attributeName).append(this.operator, that.operator)
                .append(this.conjunction, that.conjunction).append(this.values, that.values).isEquals();
    }

}
