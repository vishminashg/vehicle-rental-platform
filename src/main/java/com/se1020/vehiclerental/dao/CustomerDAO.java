package com.se1020.vehiclerental.dao;

import com.se1020.vehiclerental.model.Customer;
import com.se1020.vehiclerental.model.PremiumCustomer;
import com.se1020.vehiclerental.model.RegularCustomer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * CustomerDAO - same design as VehicleDAO, kept as a separate class (rather
 * than one generic "FileDAO" for everything) so it stays simple to read and
 * to explain line-by-line in the viva.
 *
 * File format:
 *   id|TYPE|fullName|email|phoneNumber|licenseNumber[|loyaltyPoints]
 *   TYPE is REGULAR or PREMIUM; loyaltyPoints only exists for PREMIUM rows.
 */
public class CustomerDAO {

    private final String filePath;

    public CustomerDAO(String filePath) {
        this.filePath = filePath;
    }

    public List<Customer> getAll() {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Customer customer = parseLine(line);
                if (customer != null) {
                    customers.add(customer);
                }
            }
        } catch (IOException e) {
            System.err.println("CustomerDAO: could not read " + filePath + " - " + e.getMessage());
        }
        return customers;
    }

    public Customer findById(String customerId) {
        for (Customer customer : getAll()) {
            if (customer.getCustomerId().equalsIgnoreCase(customerId)) {
                return customer;
            }
        }
        return null;
    }

    public void add(Customer customer) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(customer.toFileString());
        } catch (IOException e) {
            System.err.println("CustomerDAO: could not write to " + filePath + " - " + e.getMessage());
        }
    }

    public boolean update(Customer updatedCustomer) {
        List<Customer> customers = getAll();
        boolean found = false;
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerId().equalsIgnoreCase(updatedCustomer.getCustomerId())) {
                customers.set(i, updatedCustomer);
                found = true;
                break;
            }
        }
        if (found) {
            saveAll(customers);
        }
        return found;
    }

    public boolean delete(String customerId) {
        List<Customer> customers = getAll();
        boolean removed = customers.removeIf(c -> c.getCustomerId().equalsIgnoreCase(customerId));
        if (removed) {
            saveAll(customers);
        }
        return removed;
    }

    public String generateNextId() {
        List<Customer> customers = getAll();
        int max = 0;
        for (Customer customer : customers) {
            String digits = customer.getCustomerId().replaceAll("\\D", "");
            if (!digits.isEmpty()) {
                max = Math.max(max, Integer.parseInt(digits));
            }
        }
        return String.format("C%03d", max + 1);
    }

    // ---------- private helpers ----------

    private void saveAll(List<Customer> customers) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, false))) {
            for (Customer customer : customers) {
                writer.println(customer.toFileString());
            }
        } catch (IOException e) {
            System.err.println("CustomerDAO: could not rewrite " + filePath + " - " + e.getMessage());
        }
    }

    private Customer parseLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 6) {
            return null;
        }
        String id = p[0];
        String type = p[1];
        String fullName = p[2];
        String email = p[3];
        String phone = p[4];
        String license = p[5];

        if ("PREMIUM".equalsIgnoreCase(type)) {
            double loyaltyPoints = p.length > 6 ? Double.parseDouble(p[6]) : 0;
            return new PremiumCustomer(id, fullName, email, phone, license, loyaltyPoints);
        } else {
            return new RegularCustomer(id, fullName, email, phone, license);
        }
    }
}
