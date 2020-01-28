package ca.jrvs.apps.jdbc;

import ca.jrvs.apps.jdbc.util.DataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO extends DataAccessObject<Order> {

    private static String ORDER = "SELECT " +
            // Index: 1, 2, 3, 4
            "c.first_name, c.last_name, c.email, o.id, " +
            // Index: 5, 6, 7
            "o.creation_date, o.total_due, o.status, " +
            // Index: 8, 9, 10
            "s.first_name, s.last_name, s.email, " +
            // Index: 11, 12, 13, 14, 15, 16
            "ol.quantity, p.code, p.name, p.size, p.variety, p.price " +
            "FROM orders o " +
            "join customer c on o.customer_id=c.customer_id "+
            "join salesperson s on o.salesperson_id=s.salesperson_id " +
            "join order_item ol on ol.order_id=o.order_id " +
            "join product p on ol.product=p.product_id " +
            "where o.order_id=?";

    public OrderDAO(Connection connection) {
        super(connection);
    }

    @Override
    public Order findById(long id) {
        Order order = new Order();

        try (PreparedStatement statement = this.connection.prepareStatement(ORDER);){
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            long orderId = 0;
            List<OrderLine> orderLines = new ArrayList<>();

            while (resultSet.next()){
                if (orderId == 0) {
                    // The order only requires this information to be set once.
                    // Customer and salesperson information should not change in the
                    // same order form.
                    order.setCustomerFirstName(resultSet.getString(1));
                    order.setCustomerLastName(resultSet.getString(2));
                    order.setCustomerEmail(resultSet.getString(3));
                    order.setId(resultSet.getLong(4));
                    order.setCreationDate(new Date(resultSet.getDate(5).getTime()));
                    order.setTotalDue(resultSet.getBigDecimal(6));
                    order.setStatus(resultSet.getString(7));
                    order.setSalespersonFirstName(resultSet.getString(8));
                    order.setSalespersonLastName(resultSet.getString(9));
                    order.setSalespersonEmail(resultSet.getString(10));
                }

                OrderLine item = new OrderLine();
                item.setQuanity(resultSet.getInt(11));
                item.setProductCode(resultSet.getString(12));
                item.setProductName(resultSet.getString(13));
                item.setProductSize(resultSet.getInt(14));
                item.setProductVariety(resultSet.getString(15));
                item.setProductPrice(resultSet.getBigDecimal(16));
                orderLines.add(item);
            }
            order.setOrderLines(orderLines);
        } catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return order;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public Order update(Order dto) {
        return null;
    }

    @Override
    public Order create(Order dto) {
        return null;
    }

    @Override
    public void delete(long id) {
    }
}
