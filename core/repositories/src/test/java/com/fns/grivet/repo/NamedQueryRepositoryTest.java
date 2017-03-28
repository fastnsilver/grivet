package com.fns.grivet.repo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.query.QueryType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NamedQueryRepositoryTest {

    @Autowired
    private NamedQueryRepository repo;
    
    @Test
    // representative; a named query will only execute successfully against known tables/views
    public void testFindByName() {
        NamedQuery detached = 
                NamedQuery.builder()
                            .name("totalSpendInLastMonth") 
                            .type(QueryType.SELECT)
                            .query("SELECT SUM(AMOUNT) FROM EXPENSE WHERE YEAR(TRANSACTION_DATE) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) AND MONTH(TRANSACTION_DATE) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH)")
                            .build();
        NamedQuery expected = repo.save(detached);
        NamedQuery actual = repo.findByName("totalSpendInLastMonth");
        Assert.assertNotNull("Expected a matching NamedQuery!", actual);
        Assert.assertEquals(expected, actual);
    }
    
    @After
    public void tearDown() {
        repo.deleteAll();
    }

}
