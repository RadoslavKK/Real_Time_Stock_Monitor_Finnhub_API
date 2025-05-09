package src;

public class StockInfo {
    private String companyName;
    private String stockSymbol;
    private double currentPrice;
    private double previousPrice;
    private int amountOwned;
    private double buyPrice;
    private boolean active;

    public StockInfo(String companyName, String stockSymbol, double currentPrice, double previousPrice,
                     int amountOwned, double buyPrice, boolean active) {
        this.companyName = companyName;
        this.stockSymbol = stockSymbol;
        this.currentPrice = currentPrice;
        this.previousPrice = previousPrice;
        this.amountOwned = amountOwned;
        this.buyPrice = buyPrice;
        this.active = active;
    }

    public String getCompanyName() { return companyName; }
    public String getStockSymbol() { return stockSymbol; }
    public double getCurrentPrice() { return currentPrice; }
    public double getPreviousPrice() { return previousPrice; }
    public int getAmountOwned() { return amountOwned; }
    public double getBuyPrice() { return buyPrice; }
    public boolean isActive() { return active; }

    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public void setPreviousPrice(double previousPrice) { this.previousPrice = previousPrice; }

    // Optional setters for future flexibility
    public void setAmountOwned(int amountOwned) { this.amountOwned = amountOwned; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return String.format("%s (%s): Current=%.2f, Previous=%.2f, Owned=%d, Buy@=%.2f, Active=%b",
            companyName, stockSymbol, currentPrice, previousPrice, amountOwned, buyPrice, active);
    }
}
