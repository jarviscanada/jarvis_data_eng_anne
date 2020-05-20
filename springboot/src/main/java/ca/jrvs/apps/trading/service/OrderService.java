package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.domain.Account;
import ca.jrvs.apps.trading.model.domain.MarketOrderDto;
import ca.jrvs.apps.trading.model.domain.Position;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private AccountDao accountDao;
    private SecurityOrderDao securityOrderDao;
    private QuoteDao quoteDao;
    private PositionDao positionDao;

    @Autowired
    public OrderService(AccountDao accountDao, SecurityOrderDao securityOrderDao, QuoteDao quoteDao, PositionDao positionDao){
        this.accountDao = accountDao;
        this.securityOrderDao = securityOrderDao;
        this.quoteDao = quoteDao;
        this.positionDao = positionDao;
    }

    /**
     * Execute a market order.
     *
     * - validate the order (e.g. size and ticker)
     * - create a security order (for security_order table)
     * - handle buy/sell order:
     * (a) buy order - check account balance (calls helper method)
     * (b) sell order - check position for the ticker/symbol (calls helper method)
     * (c) updates securityOrder.status
     * - save and return security order
     *
     * @param orderDto market order
     * @return SecurityOrder from security_order table
     * @throws DataAccessException if unable to get data from the DAO
     * @throws IllegalArgumentException for invalid input.
     */
    public SecurityOrder executeMarketOrder(MarketOrderDto orderDto){
        // Verify that the order contains a valid size and ticker.
        verifyOrderSize(orderDto);
        verifyTicker(orderDto);

        Optional<Account> account = accountDao.findById(orderDto.getAccountID());
        if (!account.isPresent()){
            throw new IllegalArgumentException("There is no account associated with the account ID in the order. Please resubmit your order with an existing account.");
        }

        SecurityOrder order = new SecurityOrder();
        String orderType = orderType(orderDto);

        if (orderType.equals("buy")){
            handleBuyMarketOrder(orderDto, order, account.get());
        } else if (orderType.equals("sell")){
            handleSellMarketOrder(orderDto, order, account.get());
        } else if (orderType.equals("invalid")){
            throw new IllegalArgumentException("Cannot handle market order due to invalid order command.");
        }

        return order;
    }

    private void verifyOrderSize(MarketOrderDto orderDto){
        Integer units = orderDto.getSize();

        if (units == null){
            throw new IllegalArgumentException("Order does not specify number of units. Please resubmit your order with a specified number of units.");
        } else if (units == 0){
            throw new IllegalArgumentException("Order contains an improper size. Please correct your order form with a valid number of units.");
        }
    }

    private void verifyTicker(MarketOrderDto orderDto){
        String ticker = orderDto.getTicker().toUpperCase();

        if (ticker == null || ticker.isEmpty()){
            throw new IllegalArgumentException("Order does not include a specified ticker. Please resubmit your order with a ticker included.");
        }

        boolean existingTicker = quoteDao.findById(ticker).isPresent();
        if (!existingTicker){
            throw new IllegalArgumentException("Order does not contain a valid ticker. Please resubmit your order with a valid ticker included.");
        }
    }

    private String orderType(MarketOrderDto orderDto){
        Integer units = orderDto.getSize();
        if (units > 0){
            return "buy";
        } else if (units < 0){
            return "sell";
        } else {
            return "invalid";
        }
    }

    /**
     * Helper method that executes a buy order.
     * @param marketOrderDto user order
     * @param order (security order) to be saved into the database
     * @param account of the user
     */
    protected void handleBuyMarketOrder(MarketOrderDto marketOrderDto, SecurityOrder order, Account account){
        String ticker = order.getTicker().toUpperCase();
        Integer unitsRequested = order.getSize();
        Integer accountID = order.getAccountID();

        Position position = positionDao.findById(accountID).get();
        Integer positionAfterBuying = position.getPosition() + unitsRequested;

        Double pricePerUnit = quoteDao.findById(ticker).get().getAskPrice();
        Double total = pricePerUnit * unitsRequested;
        Double accountBalance = account.getAmount();

        order.setPrice(pricePerUnit);

        if (accountBalance >= total){
            Double updatedBalance = accountBalance - total;
            accountDao.updateAmountById(accountID, updatedBalance);
            order.setStatus("FILLED");
            order.setNotes("Order successful.");
        } else if (accountBalance < total){
            order.setStatus("CANCELLED");
            order.setNotes("Order cancelled due to insufficient funds.");
        }
    }

    /**
     * Helper method that executes a sell order.
     * @param marketOrderDto user order
     * @param order (security order) to be saved into the database
     * @param account of the user
     */
    protected void handleSellMarketOrder(MarketOrderDto marketOrderDto, SecurityOrder order, Account account){
        String ticker = order.getTicker().toUpperCase();
        Integer unitsSelling = order.getSize() * -1;
        Integer accountID = order.getAccountID();

        Position position = positionDao.findById(accountID).get();
        Integer positionAfterOrder = position.getPosition() - unitsSelling;

        Double pricePerUnit = quoteDao.findById(ticker).get().getAskPrice();
        Double total = pricePerUnit * unitsSelling;
        Double accountBalance = account.getAmount();

        order.setPrice(pricePerUnit);

        if (position != null && positionAfterOrder >= 0){
            Double updatedBalance = accountBalance + total;
            accountDao.updateAmountById(accountID, updatedBalance);
            order.setStatus("FILLED");
            order.setNotes("Order successful.");
        } else if (positionAfterOrder < 0){
            order.setStatus("CANCELLED");
            order.setNotes("Order cancelled due to insufficient position to sell requested number of units.");
        }
    }
}
