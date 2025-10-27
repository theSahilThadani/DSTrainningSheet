package Day9.entities;

public enum DiscountType {
    PERCENTAGE("percentage",0.0,100.0),
    FIXED_AMOUNT("fixed", 0.0, Double.MAX_VALUE),
    BUY_X_GET_Y("buyXGetY", 0.0, Double.MAX_VALUE),
    FREE_SHIPPING("freeShipping", 0.0, 0.0);

    private final String type;
    private final double minValue;
    private final double maxValue;

    DiscountType(String type, double minValue, double maxValue) {
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getType() { return type; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
}
