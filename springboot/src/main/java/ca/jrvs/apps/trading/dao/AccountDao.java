package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class AccountDao extends JdbcCrudDao<Account> {

    private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

    private final String TABLE_NAME = "account";
    private final String ID_COLUMN = "id";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public AccountDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME).usingGeneratedKeyColumns(ID_COLUMN);
    }

    /**
     * @return JDBC template.
     */
    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * @return JDBC insert.
     */
    @Override
    public SimpleJdbcInsert getSimpleJdbcInsert() {
        return simpleJdbcInsert;
    }

    /**
     * @return table name.
     */
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    /**
     * @return ID column name.
     */
    @Override
    public String getIdColumnName() {
        return ID_COLUMN;
    }

    /**
     * @return Account class.
     */
    @Override
    Class<Account> getEntityClass() {
        return Account.class;
    }

    /**
     * Updates specified account's balance.
     * @param id of the account
     * @param updatedBalance for the account
     */
    public void updateAmountById(Integer id, Double updatedBalance){
        Account account = findById(id).get();
        account.setAmount(updatedBalance);
    }

    @Override
    public int updateOne(Account entity) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public <S extends Account> Iterable<S> saveAll(Iterable<S> iterable) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void delete(Account account) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void deleteAll(Iterable<? extends Account> iterable) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
