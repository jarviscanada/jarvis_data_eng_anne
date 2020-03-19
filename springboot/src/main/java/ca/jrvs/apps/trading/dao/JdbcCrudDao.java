package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class JdbcCrudDao<T extends Entity<Integer>> implements CrudRepository<T, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCrudDao.class);

    abstract public JdbcTemplate getJdbcTemplate();

    abstract public SimpleJdbcInsert getSimpleJdbcInsert();

    abstract public String getTableName();

    abstract public String getIdColumnName();

    abstract Class<T> getEntityClass();

    /**
     * Save an entity and update auto-generated integer ID.
     * @param entity to be saved
     * @return saved entity
     */
    @Override
    public <S extends T> S save(S entity){
        if (existsById(entity.getId())){
            if (updateOne(entity) != 1){
                throw new DataRetrievalFailureException("Unable to update quote.");
            } else {
                addOne(entity);
            }
        }
        return entity;
    }

    /**
     * Helper method.
     * @param entity
     * @param <S>
     */
    private <S extends T> void addOne(S entity){
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(entity);

        Number newId = getSimpleJdbcInsert().executeAndReturnKey(parameterSource);
        entity.setId(newId.intValue());
    }

    abstract public int updateOne(T entity);

    @Override
    public Optional<T> findById(Integer id){
        Optional<T> entity = Optional.empty();
        String selectSQL = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + "=?";

        try{
            entity = Optional.ofNullable((T) getJdbcTemplate().queryForObject(selectSQL, BeanPropertyRowMapper.newInstance(getEntityClass()), id));
        } catch (IncorrectResultSizeDataAccessException e){
            logger.debug("Cannot find trader ID: " + id, e);
        }
        return entity;
    }

    @Override
    public boolean existsById(Integer id){
        Optional<T> entity = Optional.empty();
        String selectSQL = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumnName() + "=?";

        try {
            entity = Optional.ofNullable((T) getJdbcTemplate().queryForObject(selectSQL, BeanPropertyRowMapper.newInstance(getEntityClass()), id));
        } catch (IncorrectResultSizeDataAccessException e){
            logger.debug("ID does not exist: " + id, e);
            return false;
        }

        return true;
    }

    @Override
    public List<T> findAll(){
        Optional<T> entity = Optional.empty();

        String selectSQL = "SELECT * FROM " + getTableName();
        List<T> items = getJdbcTemplate().query(selectSQL, new BeanPropertyRowMapper<T>(getEntityClass()));
        return items;
    }

    @Override
    public List<T> findAllById(Iterable<Integer> ids){
        int idCount = 0;
        for (Integer id: ids){
            idCount++;
        }
        if (idCount == 0){
            throw new IllegalArgumentException("Given iterable is empty. There is nothing to find by ID.");
        }

        List<T> foundById = new ArrayList<T>();
        ids.forEach(id -> foundById.add(findById(id).get()));

        return foundById;
    }

    @Override
    public void deleteById(Integer id){
        if (id < 1){
            throw new IllegalArgumentException("ID cannot be negative.");
        } else if (id == null){
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }

        String deleteSQL = "DELETE * FROM " + getTableName() + " WHERE " + getIdColumnName() + "=?";
        getJdbcTemplate().update(deleteSQL, id);
    }

    @Override
    public long count(){
        String selectSQL = "SELECT COUNT(*) FROM " + getTableName();
        long count = getJdbcTemplate().queryForObject(selectSQL, Long.class);
        return count;
    }

    @Override
    public void deleteAll(){
        String deleteSQL = "DELETE FROM " + getTableName();
        getJdbcTemplate().update(deleteSQL);
    }
}
