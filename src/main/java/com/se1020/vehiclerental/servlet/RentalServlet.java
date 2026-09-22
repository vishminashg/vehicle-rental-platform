package com.se1020.vehiclerental.servlet;

import com.se1020.vehiclerental.dao.CustomerDAO;
import com.se1020.vehiclerental.dao.RentalDAO;
import com.se1020.vehiclerental.dao.VehicleDAO;
import com.se1020.vehiclerental.model.Customer;
import com.se1020.vehiclerental.model.Rental;
import com.se1020.vehiclerental.model.Vehicle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RentalServlet is the busiest servlet: a rental "uses" both a Vehicle and a
 * Customer, so this class talks to all three DAOs. It is also where the two
 * inheritance hierarchies actually pay off - creating a rental calls
 * vehicle.calculateRentalCost(days) and customer.getDiscountRate() without
 * ever needing to know or check which concrete subclass it got back.
 */
@WebServlet(name = "RentalServlet", urlPatterns = {"/rentals"})
public class RentalServlet extends HttpServlet {

    private RentalDAO rentalDAO;
    private VehicleDAO vehicleDAO;
    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        rentalDAO = new RentalDAO(getServletContext().getRealPath("/WEB-INF/data/rentals.txt"));
        vehicleDAO = new VehicleDAO(getServletContext().getRealPath("/WEB-INF/data/vehicles.txt"));
        customerDAO = new CustomerDAO(getServletContext().getRealPath("/WEB-INF/data/customers.txt"));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                showNewForm(request, response, null);
                break;
            case "return":
                processReturn(request, response, request.getParameter("id"));
                break;
            case "delete":
                cancelRental(request.getParameter("id"));
                response.sendRedirect("rentals?action=list");
                break;
            case "list":
            default:
                listRentals(request, response, null);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            createRental(request);
            response.sendRedirect("rentals?action=list");
        } catch (IllegalArgumentException e) {
            listRentals(request, response, "Could not create rental: " + e.getMessage());
        }
    }

    // ---------- helpers ----------

    private void listRentals(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        List<Rental> rentals = rentalDAO.getAll();
        request.setAttribute("rentals", rentals);
        request.setAttribute("vehicleDAO", vehicleDAO); // used in JSP to resolve names for display
        request.setAttribute("customerDAO", customerDAO);
        request.setAttribute("errorMessage", errorMessage);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/rentals/list.jsp");
        dispatcher.forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        List<Vehicle> availableVehicles = vehicleDAO.getAll().stream()
                .filter(Vehicle::isAvailable)
                .collect(Collectors.toList());
        List<Customer> customers = customerDAO.getAll();

        request.setAttribute("availableVehicles", availableVehicles);
        request.setAttribute("customers", customers);
        request.setAttribute("errorMessage", errorMessage);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/rentals/form.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * CREATE: builds a new Rental, prices it using the polymorphic
     * calculateRentalCost()/getDiscountRate() methods, then marks the
     * vehicle as RENTED so it drops out of the "available" list.
     */
    private void createRental(HttpServletRequest request) {
        String vehicleId = request.getParameter("vehicleId");
        String customerId = request.getParameter("customerId");
        LocalDate startDate = LocalDate.parse(request.getParameter("startDate"));
        LocalDate expectedReturnDate = LocalDate.parse(request.getParameter("expectedReturnDate"));

        Vehicle vehicle = vehicleDAO.findById(vehicleId);
        Customer customer = customerDAO.findById(customerId);

        if (vehicle == null || customer == null) {
            throw new IllegalArgumentException("Selected vehicle or customer no longer exists");
        }
        if (!expectedReturnDate.isAfter(startDate)) {
            throw new IllegalArgumentException("Return date must be after the start date");
        }

        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, expectedReturnDate);
        double rawCost = vehicle.calculateRentalCost(days);
        double discountedCost = rawCost * (1 - customer.getDiscountRate());

        Rental rental = new Rental(rentalDAO.generateNextId(), vehicleId, customerId,
                startDate, expectedReturnDate, null, round(discountedCost), Rental.STATUS_ACTIVE);
        rentalDAO.add(rental);

        vehicle.setStatus("RENTED");
        vehicleDAO.update(vehicle);
    }

    /**
     * UPDATE: marks a rental as returned, applies a late fee if applicable
     * (polymorphism-free this time - Rental.calculateLateFee() is the same
     * for every rental, unlike vehicle pricing/customer discounts), and
     * frees the vehicle back up for the next customer.
     */
    private void processReturn(HttpServletRequest request, HttpServletResponse response, String rentalId)
            throws ServletException, IOException {
        Rental rental = rentalDAO.findById(rentalId);
        if (rental != null && Rental.STATUS_ACTIVE.equals(rental.getStatus())) {
            LocalDate today = LocalDate.now();
            double lateFee = rental.calculateLateFee(today);

            rental.setActualReturnDate(today);
            rental.setTotalCost(round(rental.getTotalCost() + lateFee));
            rental.setStatus(Rental.STATUS_RETURNED);
            rentalDAO.update(rental);

            Vehicle vehicle = vehicleDAO.findById(rental.getVehicleId());
            if (vehicle != null) {
                vehicle.setStatus("AVAILABLE");
                vehicleDAO.update(vehicle);
            }
        }
        response.sendRedirect("rentals?action=list");
    }

    /** DELETE: cancels a rental outright and frees the vehicle if it was active. */
    private void cancelRental(String rentalId) {
        Rental rental = rentalDAO.findById(rentalId);
        if (rental != null) {
            if (Rental.STATUS_ACTIVE.equals(rental.getStatus())) {
                Vehicle vehicle = vehicleDAO.findById(rental.getVehicleId());
                if (vehicle != null) {
                    vehicle.setStatus("AVAILABLE");
                    vehicleDAO.update(vehicle);
                }
            }
            rentalDAO.delete(rentalId);
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
