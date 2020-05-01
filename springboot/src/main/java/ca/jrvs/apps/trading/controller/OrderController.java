package ca.jrvs.apps.trading.controller;

import ca.jrvs.apps.trading.model.domain.MarketOrderDto;
import ca.jrvs.apps.trading.model.domain.SecurityOrder;
import ca.jrvs.apps.trading.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(
        value="Order",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Controller
@RequestMapping("/order")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @ApiOperation(
            value = "Submit a market order.",
            notes = "Submit a market order.")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Account ID or ticker is not found."),
            @ApiResponse(code = 400, message = "Unable to process order due to bad user input.")})
    @PostMapping(path = "/marketOrder")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public SecurityOrder postMarketOrder(@RequestBody MarketOrderDto orderDto){
        try{
            return orderService.executeMarketOrder(orderDto);
        } catch (Exception e){
            throw ResponseExceptionUtil.getResponseStatusException(e);
        }
    }
}