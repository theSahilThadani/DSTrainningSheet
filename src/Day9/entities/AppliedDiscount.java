package Day9.entities;

import java.time.LocalDateTime;

public class AppliedDiscount {
    private Discount discount;
    private double discountAmount;
    private LocalDateTime appliedAt;

    public AppliedDiscount(Discount discount, double discountAmount) {
        this.discount = discount;
        this.discountAmount = discountAmount;
        this.appliedAt = LocalDateTime.now();
    }
    public Discount getDiscount() { return discount; }
    public double getDiscountAmount() { return discountAmount; }
    public LocalDateTime getAppliedAt() { return appliedAt; }

    @Override
    public String toString() {
        return String.format("%s: -₹%.2f", discount.getCode(), discountAmount);
    }
}
