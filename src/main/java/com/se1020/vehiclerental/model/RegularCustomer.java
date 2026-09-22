package com.se1020.vehiclerental.model;

/** RegularCustomer - standard member, no rental discount. */
public class RegularCustomer extends Customer {

    public RegularCustomer(String customerId, String fullName, String email,
                            String phoneNumber, String licenseNumber) {
        super(customerId, fullName, email, phoneNumber, licenseNumber);
    }

    @Override
    public double getDiscountRate() {
        return 0.0;
    }

    @Override
    public String getCustomerType() {
        return "Regular";
    }

    @Override
    public String getExtraDetails() {
        return "-";
    }

    @Override
    public String toFileString() {
        return String.join("|",
                getCustomerId(), "REGULAR", getFullName(), getEmail(),
                getPhoneNumber(), getLicenseNumber());
    }
}
