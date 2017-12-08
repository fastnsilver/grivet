package com.fns.grivet.repo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fns.grivet.query.NamedQuery;
import com.fns.grivet.query.QueryType;

@ExtendWith(SpringExtension.class)
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
        Assertions.assertNotNull(actual, "Expected a matching NamedQuery!");
        Assertions.assertEquals(expected, actual);
    }
    
    @AfterEach
    public void tearDown() {
        repo.deleteAll();
    }

}
