package com.fns.grivet.query;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fns.grivet.model.AttributeType;
import com.fns.grivet.query.DynamicQuery;
import com.fns.grivet.query.QueryBuilder;

public class DynamicQueryTest {

    private static final String BASE_JOIN = "INNER JOIN all_entity_values AS ev ON e.eid = ev.eid ";
    
    private final QueryBuilder qb = QueryBuilder.newInstance();
    private final Map<Integer, Integer> attributeToAttributeTypeMap = new HashMap<>();
    private final Map<String, Integer> attributeNameToAttributeIdMap = new HashMap<>();
    
    @Before
    public void setUp() {
        attributeToAttributeTypeMap.put(1, AttributeType.VARCHAR.getId());
        attributeToAttributeTypeMap.put(2, AttributeType.INTEGER.getId());
        attributeNameToAttributeIdMap.put("flavor", 1);
        attributeNameToAttributeIdMap.put("age", 2);
    }
    
    @Test
    public void testConstaintWithNullConjunction() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("c", "flavor|equals|Vanilla");
        DynamicQuery dq = new DynamicQuery(request.getParameterMap().entrySet(), attributeToAttributeTypeMap, attributeNameToAttributeIdMap);
        Assert.assertEquals(BASE_JOIN, qb.obtainEntityAttributeValueJoins(dq));
        Assert.assertEquals("(ev.aid = ? AND ev.v = ?)", qb.obtainEntityAttributeValueCriteria(dq));
        Assert.assertTrue(dq.asSqlParameterValues().length == 2);
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[0].getSqlType());
        Assert.assertEquals(1, dq.asSqlParameterValues()[0].getValue());
        Assert.assertEquals(Types.VARCHAR, dq.asSqlParameterValues()[1].getSqlType());
        Assert.assertEquals("Vanilla", dq.asSqlParameterValues()[1].getValue());
    }
        
    @Test
    public void testConstaintWithBetween() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("constraint", "age|between|35,46");
        DynamicQuery dq = new DynamicQuery(request.getParameterMap().entrySet(), attributeToAttributeTypeMap, attributeNameToAttributeIdMap);
        Assert.assertEquals(BASE_JOIN, qb.obtainEntityAttributeValueJoins(dq));
        Assert.assertEquals("(ev.aid = ? AND ev.v BETWEEN ? AND ?)", qb.obtainEntityAttributeValueCriteria(dq));
        Assert.assertTrue(dq.asSqlParameterValues().length == 3);
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[0].getSqlType());
        Assert.assertEquals(2, dq.asSqlParameterValues()[0].getValue());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[1].getSqlType());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[2].getSqlType());
        Assert.assertEquals(35, dq.asSqlParameterValues()[1].getValue());
        Assert.assertEquals(46, dq.asSqlParameterValues()[2].getValue());
    }
    
    @Test
    public void testConstaintWithIn() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("c", "age|in|35,46,59,76");
        DynamicQuery dq = new DynamicQuery(request.getParameterMap().entrySet(), attributeToAttributeTypeMap, attributeNameToAttributeIdMap);
        Assert.assertEquals(BASE_JOIN, qb.obtainEntityAttributeValueJoins(dq));
        Assert.assertEquals("(ev.aid = ? AND ev.v IN (?,?,?,?))", qb.obtainEntityAttributeValueCriteria(dq));
        Assert.assertTrue(dq.asSqlParameterValues().length == 5);
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[0].getSqlType());
        Assert.assertEquals(2, dq.asSqlParameterValues()[0].getValue());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[1].getSqlType());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[2].getSqlType());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[3].getSqlType());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[4].getSqlType());
        Assert.assertEquals(35, dq.asSqlParameterValues()[1].getValue());
        Assert.assertEquals(46, dq.asSqlParameterValues()[2].getValue());
        Assert.assertEquals(59, dq.asSqlParameterValues()[3].getValue());
        Assert.assertEquals(76, dq.asSqlParameterValues()[4].getValue());
    }
    
    @Test
    public void testAndConstraints() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("constraint", "age|equals|35|and");
        request.setParameter("c", "flavor|startsWith|V");
        DynamicQuery dq = new DynamicQuery(request.getParameterMap().entrySet(), attributeToAttributeTypeMap, attributeNameToAttributeIdMap);
        Assert.assertEquals(BASE_JOIN + "INNER JOIN all_entity_values AS ev1 ON ev.eid = ev1.eid ", qb.obtainEntityAttributeValueJoins(dq));
        Assert.assertEquals("(ev.aid = ? AND ev.v = ?) AND (ev1.aid = ? AND ev1.v LIKE ?)", qb.obtainEntityAttributeValueCriteria(dq));
        Assert.assertTrue(dq.asSqlParameterValues().length == 4);
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[0].getSqlType());
        Assert.assertEquals(2, dq.asSqlParameterValues()[0].getValue());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[1].getSqlType());
        Assert.assertEquals(35, dq.asSqlParameterValues()[1].getValue());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[2].getSqlType());
        Assert.assertEquals(1, dq.asSqlParameterValues()[2].getValue());
        Assert.assertEquals(Types.VARCHAR, dq.asSqlParameterValues()[3].getSqlType());
        Assert.assertEquals("V%", dq.asSqlParameterValues()[3].getValue());
    }
    
    @Test
    public void testOrConstraints() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("constraint", "age|equals|35|or");
        request.setParameter("c", "flavor|endsWith|a");
        DynamicQuery dq = new DynamicQuery(request.getParameterMap().entrySet(), attributeToAttributeTypeMap, attributeNameToAttributeIdMap);
        Assert.assertEquals(BASE_JOIN, qb.obtainEntityAttributeValueJoins(dq));
        Assert.assertEquals("(ev.aid = ? AND ev.v = ?) OR (ev.aid = ? AND ev.v LIKE ?)", qb.obtainEntityAttributeValueCriteria(dq));
        Assert.assertTrue(dq.asSqlParameterValues().length == 4);
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[0].getSqlType());
        Assert.assertEquals(2, dq.asSqlParameterValues()[0].getValue());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[1].getSqlType());
        Assert.assertEquals(35, dq.asSqlParameterValues()[1].getValue());
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[2].getSqlType());
        Assert.assertEquals(1, dq.asSqlParameterValues()[2].getValue());
        Assert.assertEquals(Types.VARCHAR, dq.asSqlParameterValues()[3].getSqlType());
        Assert.assertEquals("%a", dq.asSqlParameterValues()[3].getValue());
    }
    
    @Test
    public void testContainsConstraints() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("c", "flavor|contains|n");
        DynamicQuery dq = new DynamicQuery(request.getParameterMap().entrySet(), attributeToAttributeTypeMap, attributeNameToAttributeIdMap);
        Assert.assertEquals(BASE_JOIN, qb.obtainEntityAttributeValueJoins(dq));
        Assert.assertEquals("(ev.aid = ? AND ev.v LIKE ?)", qb.obtainEntityAttributeValueCriteria(dq));
        Assert.assertTrue(dq.asSqlParameterValues().length == 2);
        Assert.assertEquals(Types.INTEGER, dq.asSqlParameterValues()[0].getSqlType());
        Assert.assertEquals(1, dq.asSqlParameterValues()[0].getValue());
        Assert.assertEquals(Types.VARCHAR, dq.asSqlParameterValues()[1].getSqlType());
        Assert.assertEquals("%n%", dq.asSqlParameterValues()[1].getValue());
    }
    
}
