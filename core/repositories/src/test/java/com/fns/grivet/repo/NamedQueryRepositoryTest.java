package com.fns.grivet.repo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.query.QueryType;

@RunWith(SpringRunner.class)
@DataJpaTest
public class NamedQueryRepositoryTest {

    @Autowired
    private NamedQueryRepository repo;
    
    @Test
    // representative; a named query will only execute successfully against known tables/views
    public void testFindByName() {
        NamedQuery expected = new NamedQuery(
                "totalSpendInLastMonth", 
                QueryType.SELECT, 
                "SELECT SUM(AMOUNT) FROM EXPENSE WHERE YEAR(TRANSACTION_DATE) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) AND MONTH(TRANSACTION_DATE) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH)", 
                null);
        repo.save(expected);
        NamedQuery actual = repo.findByName("totalSpendInLastMonth");
        Assert.assertNotNull("Expected a matching NamedQuery!", actual);
        Assert.assertEquals(expected, actual);
    }

}
