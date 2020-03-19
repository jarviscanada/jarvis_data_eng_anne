package ca.jrvs.apps.trading.dao;

import ca.jrvs.apps.trading.model.domain.Quote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QuoteDao implements CrudRepository<Quote, String> {

    private static final String TABLE_NAME = "quote";
    private static final String ID_COLUMN_NAME = "ticker";

    private static final Logger logger = LoggerFactory.getLogger(QuoteDao.class);
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    public QuoteDao(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
        simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName(TABLE_NAME);
    }

    @Override
    public Quote save(Quote quote){
        if (existsById(quote.getTicker())){
            int updateRowNo = updateOne(quote);
            if (updateRowNo != 1){
                throw new DataRetrievalFailureException("Unable to update quote.");
            }
        } else {
            addOne(quote);
        }
        return quote;
    }

    private void addOne(Quote quote){
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(quote);
        int row = simpleJdbcInsert.execute(parameterSource);
        if (row != 1) {
            throw new IncorrectResultSizeDataAccessException("Failed to insert", 1, row);
        }
    }

    private int updateOne(Quote quote){
        String update_sql = "UPDATE quote SET last_price=?, bid_price=?, bid_size=?, ask_price=?, ask_size=? WHERE ticker=?";
        return jdbcTemplate.update(update_sql, makeUpdateValues(quote));
    }

    private Object[] makeUpdateValues(Quote quote){
        Object[] updated = {quote.getLastPrice(), quote.getBidPrice(), quote.getBidSize(), quote.getAskPrice(), quote.getAskSize(), quote.getId()};

        return updated;
    }

    @Override
    public <S extends Quote> List<S> saveAll(Iterable<S> quotes){
        // Check that there are quotes to save.
        int quoteCount = 0;
        for (S quote: quotes){
            quoteCount++;
        }
        if (quoteCount == 0){
            throw new IllegalArgumentException("Given iterable is empty. There are no quotes to save.");
        }

        List<S> quoteList = new ArrayList<S>();
        quotes.forEach(quote -> quoteList.add(quote));
        quoteList.forEach(quote -> updateOne(quote));

        return quoteList;
    }

    @Override
    public List<Quote> findAll(){
        // SELECT * FROM table_name
        String selectSQL = "SELECT * FROM " + TABLE_NAME;
        List<Quote> quotes = jdbcTemplate.query(selectSQL, BeanPropertyRowMapper.newInstance(Quote.class));
        return quotes;
    }

    @Override
    public Optional<Quote> findById(String ticker){
        // SELECT * FROM table_name WHERE TICKER='ticker'
        String selectSQL = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + "=\'" + ticker + "/'";

        List<Quote> quotes = jdbcTemplate.query(selectSQL, BeanPropertyRowMapper.newInstance(Quote.class));
        if (quotes.size() == 1){
            return Optional.ofNullable(quotes.get(0));
        }
        else {
            throw new DataRetrievalFailureException("Failed to find quote with given ticker.");
        }
    }

    @Override
    public boolean existsById(String ticker){
        // SELECT * FROM table_name WHERE ticker='ticker'
        String selectSQL = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + "=\'" + ticker + "/'";
        List<Quote> quotes = jdbcTemplate.query(selectSQL, BeanPropertyRowMapper.newInstance(Quote.class));
        if (quotes.size() == 1){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void deleteById(String ticker){
        if (ticker.isEmpty()){
            throw new IllegalArgumentException("Ticker cannot be empty.");
        } else if (ticker == null){
            throw new IllegalArgumentException("Ticker cannot be null.");
        }

        // DELETE * FROM table_name WHERE ticker=?
        String deleteSQL = "DELETE * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN_NAME + "=?";
        jdbcTemplate.update(deleteSQL, ticker);
    }

    @Override
    public long count(){
        String countSQL = "SELECT COUNT(*) FROM " + TABLE_NAME;
        long count = jdbcTemplate.queryForObject(countSQL, Long.class);
        return count;
    }

    @Override
    public void deleteAll(){
        String deleteSQL = "DELETE FROM " + TABLE_NAME;
        jdbcTemplate.update(deleteSQL);
    }

    @Override
    public Iterable<Quote> findAllById(Iterable<String> strings){
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void delete(Quote entity){
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void deleteAll(Iterable<? extends Quote> entities){
        throw new UnsupportedOperationException("Not implemented.");
    }
}
