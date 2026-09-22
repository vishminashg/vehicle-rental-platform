package com.se1020.vehiclerental.model;

/**
 * Car - a concrete Vehicle. Adds a flat daily insurance surcharge on top of the
 * base daily rate (INHERITANCE: reuses everything from Vehicle and only adds
 * what is specific to a car).
 */
public class Car extends Vehicle {

    private static final double DAILY_INSURANCE_SURCHARGE = 500.0;

    private int numberOfSeats;

    public Car(String vehicleId, String brand, String model, String registrationNumber,
               double baseDailyRate, String status, int numberOfSeats) {
        super(vehicleId, brand, model, registrationNumber, baseDailyRate, status);
        this.numberOfSeats = numberOfSeats;
    }

    @Override
    public double calculateRentalCost(int numberOfDays) {
        return (getBaseDailyRate() + DAILY_INSURANCE_SURCHARGE) * numberOfDays;
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }

    @Override
    public String getExtraDetails() {
        return numberOfSeats + " seats";
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    @Override
    public String toFileString() {
        return String.join("|",
                getVehicleId(), "CAR", getBrand(), getModel(), getRegistrationNumber(),
                String.valueOf(getBaseDailyRate()), getStatus(), String.valueOf(numberOfSeats));
    }
}
