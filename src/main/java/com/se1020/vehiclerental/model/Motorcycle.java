package com.se1020.vehiclerental.model;

/**
 * Motorcycle - a concrete Vehicle. Cheapest category: no surcharge is added,
 * it just charges the base daily rate (this is what makes calculateRentalCost
 * POLYMORPHIC across the three vehicle types).
 */
public class Motorcycle extends Vehicle {

    private String engineCapacity; // e.g. "150cc"

    public Motorcycle(String vehicleId, String brand, String model, String registrationNumber,
                       double baseDailyRate, String status, String engineCapacity) {
        super(vehicleId, brand, model, registrationNumber, baseDailyRate, status);
        this.engineCapacity = engineCapacity;
    }

    @Override
    public double calculateRentalCost(int numberOfDays) {
        return getBaseDailyRate() * numberOfDays;
    }

    @Override
    public String getVehicleType() {
        return "Motorcycle";
    }

    @Override
    public String getExtraDetails() {
        return engineCapacity;
    }

    public String getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(String engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    @Override
    public String toFileString() {
        return String.join("|",
                getVehicleId(), "MOTORCYCLE", getBrand(), getModel(), getRegistrationNumber(),
                String.valueOf(getBaseDailyRate()), getStatus(), engineCapacity);
    }
}
