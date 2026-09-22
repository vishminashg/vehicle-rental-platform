<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.se1020.vehiclerental.dao.VehicleDAO" %>
<%@ page import="com.se1020.vehiclerental.dao.CustomerDAO" %>
<%@ page import="com.se1020.vehiclerental.dao.RentalDAO" %>
<%@ page import="com.se1020.vehiclerental.model.Vehicle" %>
<%@ page import="com.se1020.vehiclerental.model.Rental" %>
<%@ page import="java.util.List" %>
<%
    // Dashboard reads straight from the three DAOs to show a quick snapshot.
    VehicleDAO vehicleDAO = new VehicleDAO(application.getRealPath("/WEB-INF/data/vehicles.txt"));
    CustomerDAO customerDAO = new CustomerDAO(application.getRealPath("/WEB-INF/data/customers.txt"));
    RentalDAO rentalDAO = new RentalDAO(application.getRealPath("/WEB-INF/data/rentals.txt"));

    List<Vehicle> allVehicles = vehicleDAO.getAll();
    long availableCount = allVehicles.stream().filter(Vehicle::isAvailable).count();
    long totalCustomers = customerDAO.getAll().size();
    List<Rental> allRentals = rentalDAO.getAll();
    long activeRentals = allRentals.stream().filter(r -> Rental.STATUS_ACTIVE.equals(r.getStatus())).count();
%>
<%@ include file="/common/header.jsp" %>

<h2 class="mb-4">Dashboard</h2>

<div class="row g-4 mb-4">
    <div class="col-md-4">
        <div class="card card-stat p-4">
            <div class="text-muted">Available Vehicles</div>
            <h2><%= availableCount %> / <%= allVehicles.size() %></h2>
            <a href="vehicles" class="stretched-link"></a>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card card-stat p-4">
            <div class="text-muted">Registered Customers</div>
            <h2><%= totalCustomers %></h2>
            <a href="customers" class="stretched-link"></a>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card card-stat p-4">
            <div class="text-muted">Active Rentals</div>
            <h2><%= activeRentals %></h2>
            <a href="rentals" class="stretched-link"></a>
        </div>
    </div>
</div>

<div class="card p-4">
    <h5>Quick actions</h5>
    <div class="d-flex gap-2 mt-3">
        <a href="vehicles?action=new" class="btn btn-primary">+ Add Vehicle</a>
        <a href="customers?action=new" class="btn btn-primary">+ Register Customer</a>
        <a href="rentals?action=new" class="btn btn-success">+ New Rental</a>
    </div>
</div>

<%@ include file="/common/footer.jsp" %>
