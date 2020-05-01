package ca.jrvs.apps.trading.model.domain;

public class Position implements Entity<Integer> {

    private Integer accountID;
    private String ticker;
    private Integer position;

    @Override
    public Integer getId() {
        return this.accountID;
    }

    @Override
    public void setId(Integer id) {
        this.accountID = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
