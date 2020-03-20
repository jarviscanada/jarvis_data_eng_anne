package ca.jrvs.apps.trading.model.domain;

public class TraderAccountView {

    private Trader trader;
    private Account account;

    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
