package com.se1020.vehiclerental.model;

/**
 * Customer is the abstract superclass for anyone who can rent a vehicle.
 * Mirrors the design of Vehicle: shared data lives here, type-specific
 * behaviour (the discount rate) is abstract and supplied by subclasses.
 */
public abstract class Customer {

    private String customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String licenseNumber;

    public Customer(String customerId, String fullName, String email,
                     String phoneNumber, String licenseNumber) {
        this.customerId = customerId;
        this.fullName = fullName;
        setEmail(email);
        this.phoneNumber = phoneNumber;
        this.licenseNumber = licenseNumber;
    }

    /** POLYMORPHISM: Regular customers get 0% off, Premium customers get 15% off. */
    public abstract double getDiscountRate();

    public abstract String getCustomerType();

    /** POLYMORPHISM: Premium shows loyalty points, Regular shows a placeholder. */
    public abstract String getExtraDetails();

    public abstract String toFileString();

    // ---------- Encapsulated accessors ----------

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        // Information hiding: basic validation kept inside the class, not
        // duplicated in every servlet that happens to set an email.
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address: " + email);
        }
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}
