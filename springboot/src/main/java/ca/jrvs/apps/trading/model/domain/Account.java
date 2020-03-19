package ca.jrvs.apps.trading.model.domain;

public class Account implements Entity<Integer> {

    private Integer id;
    private Integer traderID;
    private Float amount;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer newId) {
        this.id = newId;
    }

    public Integer getTraderID() {
        return traderID;
    }

    public void setTraderID(Integer traderID) {
        this.traderID = traderID;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
