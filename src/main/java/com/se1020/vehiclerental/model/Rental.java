package com.se1020.vehiclerental.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Rental represents one booking transaction: a specific Customer renting a
 * specific Vehicle for a number of days. It is a concrete class (not part of
 * the inheritance hierarchies above) that composes the other two - a Rental
 * "has a" vehicleId and a customerId rather than "is a" Vehicle/Customer.
 */
public class Rental {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_RETURNED = "RETURNED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private static final double LATE_FEE_PER_DAY = 750.0;

    private String rentalId;
    private String vehicleId;
    private String customerId;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate; // null while the rental is still active
    private double totalCost;
    private String status;

    public Rental(String rentalId, String vehicleId, String customerId, LocalDate startDate,
                  LocalDate expectedReturnDate, LocalDate actualReturnDate,
                  double totalCost, String status) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.startDate = startDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.totalCost = totalCost;
        this.status = status;
    }

    /**
     * Calculates a late fee if the vehicle is returned after the expected date.
     * Called by the servlet when a customer returns the vehicle; kept here
     * (not in the servlet) because "how a rental is priced" is the Rental
     * object's own responsibility, not the web layer's.
     */
    public double calculateLateFee(LocalDate returnedOn) {
        long daysLate = ChronoUnit.DAYS.between(expectedReturnDate, returnedOn);
        if (daysLate <= 0) {
            return 0.0;
        }
        return daysLate * LATE_FEE_PER_DAY;
    }

    public long getPlannedDurationDays() {
        long days = ChronoUnit.DAYS.between(startDate, expectedReturnDate);
        return Math.max(days, 1);
    }

    // ---------- Encapsulated accessors ----------

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toFileString() {
        return String.join("|",
                rentalId, vehicleId, customerId,
                startDate.toString(), expectedReturnDate.toString(),
                actualReturnDate == null ? "-" : actualReturnDate.toString(),
                String.valueOf(totalCost), status);
    }
}
