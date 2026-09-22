package com.se1020.vehiclerental.model;

/**
 * Van - a concrete Vehicle. Adds a daily cargo-handling fee on top of the base
 * rate, and tracks cargo capacity, which is meaningless for a Car or Motorcycle
 * (a good example of why the shared behaviour lives in Vehicle but the extra
 * field/behaviour lives only here).
 */
public class Van extends Vehicle {

    private static final double DAILY_CARGO_FEE = 300.0;

    private double cargoCapacityKg;

    public Van(String vehicleId, String brand, String model, String registrationNumber,
               double baseDailyRate, String status, double cargoCapacityKg) {
        super(vehicleId, brand, model, registrationNumber, baseDailyRate, status);
        this.cargoCapacityKg = cargoCapacityKg;
    }

    @Override
    public double calculateRentalCost(int numberOfDays) {
        return (getBaseDailyRate() + DAILY_CARGO_FEE) * numberOfDays;
    }

    @Override
    public String getVehicleType() {
        return "Van";
    }

    @Override
    public String getExtraDetails() {
        return cargoCapacityKg + " kg cargo";
    }

    public double getCargoCapacityKg() {
        return cargoCapacityKg;
    }

    public void setCargoCapacityKg(double cargoCapacityKg) {
        this.cargoCapacityKg = cargoCapacityKg;
    }

    @Override
    public String toFileString() {
        return String.join("|",
                getVehicleId(), "VAN", getBrand(), getModel(), getRegistrationNumber(),
                String.valueOf(getBaseDailyRate()), getStatus(), String.valueOf(cargoCapacityKg));
    }
}
