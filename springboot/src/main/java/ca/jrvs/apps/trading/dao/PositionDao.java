package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PositionDao extends JdbcCrudDao<Position>  {

    private static final Logger logger = LoggerFactory.getLogger(PositionDao.class);

    private final String TABLE_NAME = "position";
    private final String ID_COLUMN = "accountID";

    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public PositionDao(DataSource dataSource){
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
     * @return Position class.
     */
    @Override
    Class<Position> getEntityClass() {
        return Position.class;
    }

    @Override
    public int updateOne(Position entity) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public <S extends Position> Iterable<S> saveAll(Iterable<S> iterable) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void delete(Position position) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void deleteAll(Iterable<? extends Position> iterable) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
