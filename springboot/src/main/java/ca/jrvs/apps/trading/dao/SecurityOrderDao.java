package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SecurityOrderDao extends JdbcCrudDao<SecurityOrder> {

    private static final Logger logger = LoggerFactory.getLogger(TraderDao.class);

    private final String TABLE_NAME = "security_order";
    private final String ID_COLUMN = "id";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public SecurityOrderDao(DataSource dataSource){
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
     * @return SecurityOrder class.
     */
    @Override
    Class<SecurityOrder> getEntityClass() {
        return SecurityOrder.class;
    }

    /**
     * Finds all of the security orders associated with an account.
     * @param account
     * @return list of security orders associated with specified account.
     */
    public List<SecurityOrder> findAllSecurityOrders(Account account){
        List<SecurityOrder> orders = new ArrayList<SecurityOrder>();

        int accountID = account.getId();
        String selectSQL = "SELECT * FROM " + getTableName() + " WHERE account_id=?";
        if (false) {
            String filled = "AND status='FILLED'";
        }

        try{
            orders = jdbcTemplate.query(selectSQL, BeanPropertyRowMapper.newInstance(SecurityOrder.class), accountID);
        } catch (DataAccessException e){
            logger.debug("Cannot find any security orders associated with account ID: " + accountID + e);
        }

        return orders;
    }

    @Override
    public int updateOne(SecurityOrder entity) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public <S extends SecurityOrder> Iterable<S> saveAll(Iterable<S> iterable) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void delete(SecurityOrder securityOrder) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void deleteAll(Iterable<? extends SecurityOrder> iterable) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}