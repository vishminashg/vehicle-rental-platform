package com.se1020.vehiclerental.dao;

import com.se1020.vehiclerental.model.Car;
import com.se1020.vehiclerental.model.Motorcycle;
import com.se1020.vehiclerental.model.Van;
import com.se1020.vehiclerental.model.Vehicle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * VehicleDAO is the ONLY class that knows vehicles.txt exists. Every servlet
 * talks to vehicles through these methods (add/getAll/findById/update/delete)
 * instead of opening files itself - this is INFORMATION HIDING: the file
 * format could change completely and nothing outside this class would need
 * to change.
 *
 * File format (one vehicle per line, fields separated by "|"):
 *   id|TYPE|brand|model|registrationNumber|baseDailyRate|status|extraField
 *   TYPE is CAR, MOTORCYCLE or VAN, and decides how the extra field is read.
 */
public class VehicleDAO {

    private final String filePath;

    public VehicleDAO(String filePath) {
        this.filePath = filePath;
    }

    /** READ - returns every vehicle currently stored in the file. */
    public List<Vehicle> getAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Vehicle vehicle = parseLine(line);
                if (vehicle != null) {
                    vehicles.add(vehicle);
                }
            }
        } catch (IOException e) {
            System.err.println("VehicleDAO: could not read " + filePath + " - " + e.getMessage());
        }
        return vehicles;
    }

    /** READ - finds a single vehicle by its id, or null if it doesn't exist. */
    public Vehicle findById(String vehicleId) {
        for (Vehicle vehicle : getAll()) {
            if (vehicle.getVehicleId().equalsIgnoreCase(vehicleId)) {
                return vehicle;
            }
        }
        return null;
    }

    /** CREATE - appends a new vehicle to the file. */
    public void add(Vehicle vehicle) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(vehicle.toFileString());
        } catch (IOException e) {
            System.err.println("VehicleDAO: could not write to " + filePath + " - " + e.getMessage());
        }
    }

    /** UPDATE - rewrites the whole file with one vehicle's details replaced. */
    public boolean update(Vehicle updatedVehicle) {
        List<Vehicle> vehicles = getAll();
        boolean found = false;
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getVehicleId().equalsIgnoreCase(updatedVehicle.getVehicleId())) {
                vehicles.set(i, updatedVehicle);
                found = true;
                break;
            }
        }
        if (found) {
            saveAll(vehicles);
        }
        return found;
    }

    /** DELETE - rewrites the whole file leaving the given vehicle out. */
    public boolean delete(String vehicleId) {
        List<Vehicle> vehicles = getAll();
        boolean removed = vehicles.removeIf(v -> v.getVehicleId().equalsIgnoreCase(vehicleId));
        if (removed) {
            saveAll(vehicles);
        }
        return removed;
    }

    /** Generates the next sequential id, e.g. V001, V002, ... */
    public String generateNextId() {
        List<Vehicle> vehicles = getAll();
        int max = 0;
        for (Vehicle vehicle : vehicles) {
            String digits = vehicle.getVehicleId().replaceAll("\\D", "");
            if (!digits.isEmpty()) {
                max = Math.max(max, Integer.parseInt(digits));
            }
        }
        return String.format("V%03d", max + 1);
    }

    // ---------- private helpers ----------

    private void saveAll(List<Vehicle> vehicles) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (Vehicle vehicle : vehicles) {
                writer.println(vehicle.toFileString());
            }
        } catch (IOException e) {
            System.err.println("VehicleDAO: could not rewrite " + filePath + " - " + e.getMessage());
        }
    }

    /**
     * Turns one line of text back into the correct concrete subclass.
     * This is the mirror image of polymorphism at read time: the TYPE field
     * tells us whether to build a Car, Motorcycle or Van.
     */
    private Vehicle parseLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 7) {
            return null; // malformed line - skip it rather than crash the page
        }
        String id = p[0];
        String type = p[1];
        String brand = p[2];
        String model = p[3];
        String regNumber = p[4];
        double dailyRate = Double.parseDouble(p[5]);
        String status = p[6];
        String extra = p.length > 7 ? p[7] : "";

        switch (type.toUpperCase()) {
            case "CAR":
                int seats = extra.isEmpty() ? 4 : Integer.parseInt(extra);
                return new Car(id, brand, model, regNumber, dailyRate, status, seats);
            case "MOTORCYCLE":
                return new Motorcycle(id, brand, model, regNumber, dailyRate, status, extra);
            case "VAN":
                double cargo = extra.isEmpty() ? 0 : Double.parseDouble(extra);
                return new Van(id, brand, model, regNumber, dailyRate, status, cargo);
            default:
                return null;
        }
    }
}
