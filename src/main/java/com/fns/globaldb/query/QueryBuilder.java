package com.fns.globaldb.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

public class QueryBuilder {

    private StringBuffer select = new StringBuffer();
    private StringBuffer from = new StringBuffer();
    private StringBuffer where = new StringBuffer();
    private StringBuffer orderBy = new StringBuffer();
    
    private QueryBuilder() {
        constructBaseFindStatement();
    }
    
    public static QueryBuilder newInstance() {
        return new QueryBuilder();
    }
    
    private void constructBaseFindStatement() {
        select.append("SELECT eva.eid AS eid, eva.aid AS attribute_id, a.name AS attribute_name, eva.v AS attribute_value, eva.created_time AS created_time ");
        from.append("FROM all_entity_values AS eva, attribute AS a ");
        where.append("WHERE eva.eid = ANY (");
    }
    
    public QueryBuilder appendCreatedTimeRange() {
        where.append("SELECT eid FROM entity WHERE cid = ?) ");
        where.append("AND eva.aid = a.id ");
        where.append("AND eva.created_time BETWEEN ? AND ?");
        return this;
    }
    
    public QueryBuilder append(DynamicQuery query) {
        where.append("SELECT DISTINCT e.eid ");
        where.append("FROM entity AS e ");
        where.append(obtainEntityAttributeValueJoins(query));
        where.append("WHERE e.cid = ? ");
        where.append("AND (");
        where.append(obtainEntityAttributeValueCriteria(query));
        where.append(")) ");
        where.append("AND eva.aid = a.id ");
        return this;
    }
    
    String obtainEntityAttributeValueJoins(DynamicQuery query) {
        StringBuffer joins = new StringBuffer();
        joins.append("INNER JOIN all_entity_values AS ev ON e.eid = ev.eid ");
        boolean isAndConjuncted = query.containsAndConjunction();
        if (isAndConjuncted) {
            int numberOfJoins = query.getConstraints().size();
            for (int i = 1; i < numberOfJoins; i++) {
                if (i == 1) {
                  joins.append(String.format("INNER JOIN all_entity_values AS ev%d ON ev.eid = ev%d.eid ", i, i));  
                } else {
                    joins.append(String.format("INNER JOIN all_entity_values AS ev%d ON ev%d.eid = ev%d.eid ", i, i-1, i)); 
                }
            }
        }
        return joins.toString();
    }
        
    String obtainEntityAttributeValueCriteria(DynamicQuery query) {
        boolean isAndConjuncted = query.containsAndConjunction();
        StringBuffer sb = new StringBuffer();
        List<Constraint> constraints = query.getConstraints();
        int i = 0;
        String suffix = "";
        String conj = null;
        for (Constraint c: constraints) {
            if (i > 0 && isAndConjuncted) {
               suffix = String.valueOf(i); 
            }
            sb.append(obtainCriterionFromConstraint(c, suffix));
            if (c.getConjunction() != null) {
                conj = c.getConjunction().getName();
            } else {
               conj = Conjunction.OR.getName(); // default
            }
            sb.append(conj);
            sb.append(" ");
            i++;
        }
        String rawCriteria = sb.toString().trim();
        String result = rawCriteria;
        if (rawCriteria.endsWith(Conjunction.AND.getName())) {
            result = rawCriteria.substring(0, rawCriteria.lastIndexOf(Conjunction.AND.getName())).trim();
        }
        if (rawCriteria.endsWith(Conjunction.OR.getName())) {
            result = rawCriteria.substring(0, rawCriteria.lastIndexOf(Conjunction.OR.getName())).trim();
        }
        return result;
    }

    private String obtainCriterionFromConstraint(Constraint c, String suffix) {
        StringBuffer clause = new StringBuffer();
        clause.append(String.format("(ev%s.aid = ?", suffix));
        clause.append(String.format(" AND ev%s.v ", suffix));
        clause.append(c.getOperator().getSymbol());
        clause.append(" ");
        if (c.getOperator().equals(Operator.BETWEEN)) {
            clause.append("?");
            clause.append(" AND ");
            clause.append("?");
        } else if (c.getOperator().equals(Operator.IN)) {
            clause.append("(");
            List<String> params = new ArrayList<>();
            Arrays.stream(c.getValues()).forEach(v -> params.add("?"));
            clause.append(StringUtils.collectionToCommaDelimitedString(params));
            clause.append(")");
        } else {
           clause.append("?"); 
        }
        clause.append(") ");
        return clause.toString();
    }
    
    public QueryBuilder appendToWhereClause(String sql) {
        where.append(sql);
        return this;
    }
    
    public QueryBuilder appendToOrderByClause(String sql) {
        orderBy.append(sql);
        return this;
    }
    
    public String build() {
        return String.format("%s %s %s %s", select.toString(), from.toString(), where.toString(), orderBy.toString());
    }
}
