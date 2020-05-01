package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.dao.AccountDao;
import ca.jrvs.apps.trading.dao.PositionDao;
import ca.jrvs.apps.trading.dao.QuoteDao;
import ca.jrvs.apps.trading.dao.SecurityOrderDao;
import ca.jrvs.apps.trading.model.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    @Captor
    ArgumentCaptor<SecurityOrder> captorSecurityOrder;

    @Mock
    private AccountDao accountDao;
    @Mock
    private SecurityOrderDao securityOrderDao;
    @Mock
    private QuoteDao quoteDao;
    @Mock
    private PositionDao positionDao;

    @InjectMocks
    private OrderService orderService;

    private MarketOrderDto marketOrderDto;
    private Account account;
    private Quote quote;
    private Position position;

    @Before
    public void setup(){
        String ticker = "AAPL";
        int accountID = 2020;

        marketOrderDto = new MarketOrderDto();
        marketOrderDto.setAccountID(accountID);
        marketOrderDto.setTicker(ticker);

        account = new Account();
        account.setId(accountID);
        account.setTraderID(accountID);
        account.setAmount(25000d);

        quote = new Quote();
        quote.setTicker(ticker);
        quote.setAskPrice(285d);
        quote.setAskSize(10);
        quote.setBidPrice(275.03);
        quote.setBidSize(20);

        position = new Position();
        position.setId(accountID);
        position.setPosition(100);
        position.setTicker(ticker);

        captorSecurityOrder = ArgumentCaptor.forClass(SecurityOrder.class);
    }

    @Test
    public void executeBuyMarketOrder(){
        int buySize = 10;
        marketOrderDto.setSize(buySize);

        when(accountDao.findById(2020)).thenReturn(Optional.of(account));
        when(quoteDao.findById("AAPL")).thenReturn(Optional.of(quote));

        orderService.executeMarketOrder(marketOrderDto);
        verify(securityOrderDao).save(captorSecurityOrder.capture());

        assertEquals(quote.getTicker(), captorSecurityOrder.getValue().getTicker());
        assertEquals(Long.valueOf(buySize), captorSecurityOrder.getValue().getSize());
        assertEquals(quote.getAskPrice(), captorSecurityOrder.getValue().getPrice());
        assertEquals("FILLED", captorSecurityOrder.getValue().getStatus());
    }

    @Test
    public void executeSellMarketOrder(){
        int sellSize = 10;
        int positionAfter = position.getPosition() - sellSize;
        marketOrderDto.setSize(sellSize);

        when(accountDao.findById(2020)).thenReturn(Optional.of(account));
        when(quoteDao.findById("AAPL")).thenReturn(Optional.of(quote));
        when(positionDao.findById(2020)).thenReturn(Optional.of(position));

        assertEquals(quote.getTicker(), captorSecurityOrder.getValue().getTicker());
        assertEquals(Long.valueOf(sellSize), captorSecurityOrder.getValue().getSize());
        assertEquals(quote.getBidPrice(), captorSecurityOrder.getValue().getPrice());
        assertEquals(Long.valueOf(positionAfter), position.getPosition());
        assertEquals("FILLED", captorSecurityOrder.getValue().getStatus());
    }

}
