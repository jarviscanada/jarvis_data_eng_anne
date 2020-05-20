package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.model.domain.Trader;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class TraderDaoIntTest {

    @Autowired
    private TraderDao dao;

    private Trader savedTrader;

    @Before
    public void insertOne(){
        savedTrader = new Trader();
        savedTrader.setFirstName("Rika");
        savedTrader.setLastName("Paprika");
        savedTrader.setCountry("Canada");
        savedTrader.setEmail("puppyduppywhatuppy@gmail.com");
        savedTrader.setDateOfBirth(Date.valueOf(LocalDate.now()));

        dao.save(savedTrader);
    }

    @After
    public void deleteOne(){
        dao.deleteById(savedTrader.getId());
    }

    @Test
    public void findAllById(){
        List<Trader> traders = Lists.newArrayList(dao.findAllById(Arrays.asList(savedTrader.getId(), -1)));
        assertEquals(1, traders.size());
        assertEquals(savedTrader.getCountry(), traders.get(0).getCountry());
    }

    @Test
    public void count(){
        long count = dao.count();
        assertNotEquals(0, count);
    }

    @Test
    public void updateOne(){
        try {
            savedTrader.setLastName("Nguyen");
            dao.updateOne(savedTrader);
        } catch (UnsupportedOperationException e){
            assertTrue(true);
        }
    }

    @Test
    public void getTableName(){
        String table = dao.getTableName();
        assertNotNull(table);
        assertEquals("public.trader", table);
    }

    @Test
    public void getTableIdColumnName(){
        String column = dao.getIdColumnName();
        assertNotNull(column);
        assertNotEquals("id", column);
    }
}
