package com.se1020.vehiclerental.model;

/**
 * Vehicle is the abstract superclass for every rentable vehicle in the system.
 *
 * OOP concepts demonstrated here:
 *  - ABSTRACTION: Vehicle cannot be instantiated directly (it only makes sense as a
 *    Car, Motorcycle or Van). It declares abstract methods that force every subclass
 *    to define its own pricing rule and its own type label.
 *  - ENCAPSULATION: all fields are private; the outside world can only read/change
 *    them through the public getters/setters below.
 *  - INFORMATION HIDING: setters validate input (e.g. a negative daily rate is
 *    rejected) so bad data can never leak into the object from a servlet or JSP.
 */
public abstract class Vehicle {

    private String vehicleId;
    private String brand;
    private String model;
    private String registrationNumber;
    private double baseDailyRate;
    private String status; // AVAILABLE | RENTED | MAINTENANCE

    public Vehicle(String vehicleId, String brand, String model, String registrationNumber,
                    double baseDailyRate, String status) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.registrationNumber = registrationNumber;
        setBaseDailyRate(baseDailyRate);
        this.status = status;
    }

    /**
     * ABSTRACTION + POLYMORPHISM: each subclass calculates its own rental cost
     * (a Car adds an insurance surcharge, a Van adds a cargo fee, a Motorcycle
     * does not). Calling code just calls vehicle.calculateRentalCost(days) and
     * gets the correct behaviour for whatever concrete type it actually holds.
     */
    public abstract double calculateRentalCost(int numberOfDays);

    /** Lets the UI show a human-readable type without using instanceof/casts. */
    public abstract String getVehicleType();

    /**
     * POLYMORPHISM: lets a JSP print "5 seats" / "150cc" / "1200kg cargo"
     * for whatever concrete vehicle it is holding, without ever needing an
     * instanceof check or a cast in the view layer.
     */
    public abstract String getExtraDetails();

    /** Each subclass knows how to serialise its own extra field(s) to text. */
    public abstract String toFileString();

    // ---------- Encapsulated accessors ----------

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public double getBaseDailyRate() {
        return baseDailyRate;
    }

    public void setBaseDailyRate(double baseDailyRate) {
        if (baseDailyRate < 0) {
            throw new IllegalArgumentException("Daily rate cannot be negative");
        }
        this.baseDailyRate = baseDailyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status);
    }
}
