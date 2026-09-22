package com.se1020.vehiclerental.dao;

import com.se1020.vehiclerental.model.Rental;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RentalDAO - file read/write for rentals.txt.
 *
 * File format:
 *   id|vehicleId|customerId|startDate|expectedReturnDate|actualReturnDate|totalCost|status
 *   actualReturnDate is the literal text "-" while the rental is still ACTIVE.
 */
public class RentalDAO {

    private final String filePath;

    public RentalDAO(String filePath) {
        this.filePath = filePath;
    }

    public List<Rental> getAll() {
        List<Rental> rentals = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Rental rental = parseLine(line);
                if (rental != null) {
                    rentals.add(rental);
                }
            }
        } catch (IOException e) {
            System.err.println("RentalDAO: could not read " + filePath + " - " + e.getMessage());
        }
        return rentals;
    }

    public Rental findById(String rentalId) {
        for (Rental rental : getAll()) {
            if (rental.getRentalId().equalsIgnoreCase(rentalId)) {
                return rental;
            }
        }
        return null;
    }

    public void add(Rental rental) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(rental.toFileString());
        } catch (IOException e) {
            System.err.println("RentalDAO: could not write to " + filePath + " - " + e.getMessage());
        }
    }

    public boolean update(Rental updatedRental) {
        List<Rental> rentals = getAll();
        boolean found = false;
        for (int i = 0; i < rentals.size(); i++) {
            if (rentals.get(i).getRentalId().equalsIgnoreCase(updatedRental.getRentalId())) {
                rentals.set(i, updatedRental);
                found = true;
                break;
            }
        }
        if (found) {
            saveAll(rentals);
        }
        return found;
    }

    public boolean delete(String rentalId) {
        List<Rental> rentals = getAll();
        boolean removed = rentals.removeIf(r -> r.getRentalId().equalsIgnoreCase(rentalId));
        if (removed) {
            saveAll(rentals);
        }
        return removed;
    }

    public String generateNextId() {
        List<Rental> rentals = getAll();
        int max = 0;
        for (Rental rental : rentals) {
            String digits = rental.getRentalId().replaceAll("\\D", "");
            if (!digits.isEmpty()) {
                max = Math.max(max, Integer.parseInt(digits));
            }
        }
        return String.format("R%03d", max + 1);
    }

    // ---------- private helpers ----------

    private void saveAll(List<Rental> rentals) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (Rental rental : rentals) {
                writer.println(rental.toFileString());
            }
        } catch (IOException e) {
            System.err.println("RentalDAO: could not rewrite " + filePath + " - " + e.getMessage());
        }
    }

    private Rental parseLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 8) {
            return null;
        }
        String id = p[0];
        String vehicleId = p[1];
        String customerId = p[2];
        LocalDate startDate = LocalDate.parse(p[3]);
        LocalDate expectedReturnDate = LocalDate.parse(p[4]);
        LocalDate actualReturnDate = "-".equals(p[5]) ? null : LocalDate.parse(p[5]);
        double totalCost = Double.parseDouble(p[6]);
        String status = p[7];
        return new Rental(id, vehicleId, customerId, startDate, expectedReturnDate,
                actualReturnDate, totalCost, status);
    }
}
