package ca.jrvs.apps.trading.model.domain;

public class SecurityOrder implements Entity<Integer> {

    private Integer id;
    private Integer accountID;
    private String status;
    private String ticker;
    private Integer size;
    private Double price;
    private String notes;


    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer newId) {
        this.id = newId;
    }

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
