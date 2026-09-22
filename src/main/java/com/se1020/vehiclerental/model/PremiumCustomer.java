package com.se1020.vehiclerental.model;

/** PremiumCustomer - a loyalty member who gets a 15% discount on every rental. */
public class PremiumCustomer extends Customer {

    private static final double DISCOUNT_RATE = 0.15;

    private double loyaltyPoints;

    public PremiumCustomer(String customerId, String fullName, String email, String phoneNumber,
                            String licenseNumber, double loyaltyPoints) {
        super(customerId, fullName, email, phoneNumber, licenseNumber);
        this.loyaltyPoints = loyaltyPoints;
    }

    @Override
    public double getDiscountRate() {
        return DISCOUNT_RATE;
    }

    @Override
    public String getCustomerType() {
        return "Premium";
    }

    @Override
    public String getExtraDetails() {
        return loyaltyPoints + " pts";
    }

    public double getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(double loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    @Override
    public String toFileString() {
        return String.join("|",
                getCustomerId(), "PREMIUM", getFullName(), getEmail(),
                getPhoneNumber(), getLicenseNumber(), String.valueOf(loyaltyPoints));
    }
}
