package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.TestConfig;
import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.dao.TraderDao;
import ca.jrvs.apps.trading.model.domain.*;
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
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfig.class})
@Sql({"classpath:schema.sql"})
public class TraderAccountServiceIntTest {

    private TraderAccountView savedView;
    @Autowired
    private TraderAccountService traderAccountService;
    @Autowired
    private SecurityOrderDao securityOrderDao;
    @Autowired
    private PositionDao positionDao;
    @Autowired
    private TraderDao traderDao;
    @Autowired
    private AccountDao accountDao;

    Trader sampleTrader;

    @Before
    public void setup(){
        accountDao.deleteAll();
        traderDao.deleteAll();
        securityOrderDao.deleteAll();

        sampleTrader.setFirstName("Rika");
        sampleTrader.setLastName("Rikerdog");
        sampleTrader.setDateOfBirth(Date.valueOf(LocalDate.now()));
        sampleTrader.setEmail("rikapaprika@gmail.com");
        sampleTrader.setCountry("Canada");

        savedView = traderAccountService.createTraderAndAccount(sampleTrader);
        Integer id = savedView.getAccount().getId();
        sampleTrader.setId(id);
    }

    @Test
    public void createTraderAndAccount(){
        Trader validTrader;
        Trader invalidTrader;
        Integer id;

        validTrader = sampleTrader;
        validTrader.setId(null);

        savedView = traderAccountService.createTraderAndAccount(validTrader);

        assertNotNull(savedView);
        assertNotNull(savedView.getAccount().getId());
        assertNotNull(savedView.getTrader().getId());
        assertNotEquals(0, accountDao.findAll());
        assertNotEquals(0, traderDao.findAll());
        assertEquals(Double.valueOf(0), savedView.getAccount().getAmount());

        try { // creating account/trader with a profile missing a last name (null).
            invalidTrader = sampleTrader;
            invalidTrader.setId(null);
            invalidTrader.setCountry(null);
            invalidTrader.setLastName(null);
            traderAccountService.createTraderAndAccount(invalidTrader);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // creating account/trader with a profile missing an e-mail address (empty string).
            invalidTrader = sampleTrader;
            invalidTrader.setId(null);
            invalidTrader.setEmail("");
            traderAccountService.createTraderAndAccount(invalidTrader);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try{ // creating account/trader with a profile with an invalid e-mail address.
            invalidTrader = sampleTrader;
            invalidTrader.setId(null);
            invalidTrader.setEmail("@gmail.com");
            traderAccountService.createTraderAndAccount(invalidTrader);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // creating account/trader with a profile with an invalid country field.
            invalidTrader = sampleTrader;
            invalidTrader.setId(null);
            invalidTrader.setCountry("Canadia");
            traderAccountService.createTraderAndAccount(invalidTrader);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try{ // creating account/trader with a profile with a non-null ID (assumed existing).
            invalidTrader = sampleTrader;
            invalidTrader.setId(2020);
            traderAccountService.createTraderAndAccount(invalidTrader);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void deposit(){
        Account account;
        Account invalidAccountOperation;
        Double depositing = 423.00;
        Integer id = sampleTrader.getId();

        account = traderAccountService.deposit(id, depositing);
        assertEquals(depositing, account.getAmount());

        try{ // depositing a negative funds.
            Double invalidFunds = -10d;
            invalidAccountOperation = traderAccountService.deposit(id, invalidFunds);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try{ // depositing into an unspecified account.
            invalidAccountOperation = traderAccountService.deposit(null, depositing);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // depositing into a non-existent account id.
            invalidAccountOperation = traderAccountService.deposit(423, depositing);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void withdraw(){
        Account account;
        Account invalidAccountOperation;
        Double withdrawing = 23d;
        Integer id = sampleTrader.getId();

        account = traderAccountService.withdraw(id, withdrawing);
        assertEquals(Double.valueOf(400.0), account.getAmount());

        try{ // withdrawing more than you have in the account.
            Double withdrawingTooMuch = 500d;
            invalidAccountOperation = traderAccountService.withdraw(id, withdrawingTooMuch);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // withdrawing from an invalid account id.
            invalidAccountOperation = traderAccountService.withdraw(100, withdrawing);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // withdrawing from an unspecified account.
            invalidAccountOperation = traderAccountService.withdraw(null, withdrawing);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @Test
    public void delete(){
        Trader traderToBeDeleted = sampleTrader;
        traderToBeDeleted.setId(null);
        TraderAccountView view = traderAccountService.createTraderAndAccount(traderToBeDeleted);
        Integer id = view.getAccount().getId();

        Position position = new Position();
        position.setId(id);
        position.setPosition(0);
        position.setTicker("AAPL");
        positionDao.save(position);

        SecurityOrder securityOrder = new SecurityOrder();
        securityOrder.setAccountID(id);
        securityOrder.setSize(10);
        securityOrder.setPrice(100d);
        securityOrder.setTicker("AAPL");
        securityOrder.setStatus("FILLED");
        securityOrder.setNotes("n/a");
        securityOrderDao.save(securityOrder);

        traderAccountService.deleteTraderByID(id);

        Optional<Trader> trader = traderDao.findById(id);
        Optional<Account> account = accountDao.findById(id);
        assertFalse(trader.isPresent());
        assertFalse(account.isPresent());
        assertEquals(0, securityOrderDao.count());

        try { // when account contains a non-zero balance
            Trader testTrader = sampleTrader;
            TraderAccountView testView = traderAccountService.createTraderAndAccount(testTrader);
            Account testAccount = testView.getAccount();
            testAccount.setAmount(100d);
            int testAccountId = testAccount.getId();

            traderAccountService.deleteTraderByID(testAccountId);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // when account has an associated open position with it
            Trader testTrader = sampleTrader;
            TraderAccountView testView = traderAccountService.createTraderAndAccount(testTrader);
            Account testAccount = testView.getAccount();
            int testAccountId = testAccount.getId();

            Position testPosition = position;
            position.setId(testAccountId);
            position.setPosition(10);

            traderAccountService.deleteTraderByID(testAccountId);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // invalid trader (null)
            traderAccountService.deleteTraderByID(null);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        try { // invalid trader (does not exist)
            traderAccountService.deleteTraderByID(20200423);
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
    }

    @After
    public void cleanup(){
        Integer id = sampleTrader.getId();
        traderAccountService.deleteTraderByID(id);
    }
}
