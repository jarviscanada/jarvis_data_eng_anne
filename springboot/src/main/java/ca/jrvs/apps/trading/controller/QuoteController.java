package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.model.domain.IexQuote;
import ca.jrvs.apps.trading.model.domain.Quote;
import ca.jrvs.apps.trading.service.QuoteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(
        value = "quote",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Controller
@RequestMapping("/quote")
public class QuoteController {

    private QuoteService quoteService;

    @Autowired
    public QuoteController(QuoteService quoteService){
        this.quoteService = quoteService;
    }

    @ApiOperation(
            value = "Show iexQuote",
            notes = "Show iexQuote for given ticker.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Ticker is not found.")})
    @GetMapping(path = "/iex/ticker/{ticker}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IexQuote getQuote(@PathVariable String ticker){
        try{
            return quoteService.findIexQuoteByTicker(ticker);
        } catch (Exception e){
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

    @ApiOperation(
            value = "Update quote table using IEX data.",
            notes = "Update all quotes in the quote table. Use IEX trading API as market data source.")
    @PutMapping(path = "/iexMarketData")
    @ResponseStatus(HttpStatus.OK)
    public void updateMarketData(){
        try{
            quoteService.updateMarketData();
        } catch (Exception e){
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

    @ApiOperation(
            value = "Update a given quote in the quote table.",
            notes = "Manually updates a quote in the quote table using IEX market data.")
    @PutMapping(path = "/")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Quote putQuote(@RequestBody Quote quote){
        try{
            return quoteService.saveQuote(quote);
        } catch (Exception e){
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

    @ApiOperation(
            value = "Add a new ticker to the dailyList (quote table).",
            notes = "Add a new ticker/symbol to the quote table so trader can trade this securely.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Ticker is not found in IEX system.")
    })
    @PostMapping(path = "/tickerId/{tickerId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Quote createQuote(@PathVariable String tickerId){
        try{
            return quoteService.saveQuote(tickerId);
        } catch (Exception e){
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }

    @ApiOperation(
            value = "Show the dailyList (quote table).",
            notes = "Show dailyList for this trading system."
    )
    @GetMapping(path = "/dailyList")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Quote> getDailyList(){
        try{
            return quoteService.findAllQuotes();
        } catch (Exception e){
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    };
}