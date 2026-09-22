package com.se1020.vehiclerental.servlet;

import com.se1020.vehiclerental.dao.VehicleDAO;
import com.se1020.vehiclerental.model.Car;
import com.se1020.vehiclerental.model.Motorcycle;
import com.se1020.vehiclerental.model.Van;
import com.se1020.vehiclerental.model.Vehicle;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * VehicleServlet routes every request under /vehicles to the right action.
 * It never touches the file system directly - all persistence goes through
 * VehicleDAO. This keeps the web layer (servlet) separate from the data
 * layer (DAO) and the business objects (model), which is the layering the
 * viva will expect you to be able to explain.
 */
@WebServlet(name = "VehicleServlet", urlPatterns = {"/vehicles"})
public class VehicleServlet extends HttpServlet {

    private VehicleDAO vehicleDAO;

    @Override
    public void init() throws ServletException {
        String dataFilePath = getServletContext().getRealPath("/WEB-INF/data/vehicles.txt");
        vehicleDAO = new VehicleDAO(dataFilePath);
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
                showForm(request, response, null);
                break;
            case "edit":
                Vehicle vehicle = vehicleDAO.findById(request.getParameter("id"));
                showForm(request, response, vehicle);
                break;
            case "delete":
                vehicleDAO.delete(request.getParameter("id"));
                response.sendRedirect("vehicles?action=list");
                break;
            case "list":
            default:
                listVehicles(request, response, null);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            Vehicle vehicle = buildVehicleFromRequest(request);

            if ("update".equals(action)) {
                vehicleDAO.update(vehicle);
            } else { // "add"
                vehicle.setVehicleId(vehicleDAO.generateNextId());
                vehicleDAO.add(vehicle);
            }
            response.sendRedirect("vehicles?action=list");

        } catch (IllegalArgumentException e) {
            // Validation failed (e.g. negative rate) - show the list again with an error message
            listVehicles(request, response, "Could not save vehicle: " + e.getMessage());
        }
    }

    // ---------- helpers ----------

    private void listVehicles(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        List<Vehicle> vehicles = vehicleDAO.getAll();
        request.setAttribute("vehicles", vehicles);
        request.setAttribute("errorMessage", errorMessage);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/vehicles/list.jsp");
        dispatcher.forward(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Vehicle vehicle)
            throws ServletException, IOException {
        request.setAttribute("vehicle", vehicle);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/vehicles/form.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Reads the submitted form fields and builds the correct concrete subclass.
     * This is where POLYMORPHISM meets the web layer: the "type" radio button
     * on the form decides whether we end up with a Car, Motorcycle or Van.
     */
    private Vehicle buildVehicleFromRequest(HttpServletRequest request) {
        String id = request.getParameter("vehicleId"); // ignored for "add", used for "update"
        String type = request.getParameter("type");
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String registrationNumber = request.getParameter("registrationNumber");
        double baseDailyRate = Double.parseDouble(request.getParameter("baseDailyRate"));
        String status = request.getParameter("status");
        String extra = request.getParameter("extraField");

        Vehicle vehicle;
        switch (type.toUpperCase()) {
            case "CAR":
                int seats = extra == null || extra.isEmpty() ? 4 : Integer.parseInt(extra);
                vehicle = new Car(id, brand, model, registrationNumber, baseDailyRate, status, seats);
                break;
            case "MOTORCYCLE":
                vehicle = new Motorcycle(id, brand, model, registrationNumber, baseDailyRate, status, extra);
                break;
            case "VAN":
                double cargo = extra == null || extra.isEmpty() ? 0 : Double.parseDouble(extra);
                vehicle = new Van(id, brand, model, registrationNumber, baseDailyRate, status, cargo);
                break;
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
        return vehicle;
    }
}
