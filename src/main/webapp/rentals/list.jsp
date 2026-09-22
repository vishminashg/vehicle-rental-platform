<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.se1020.vehiclerental.dao.VehicleDAO" %>
<%@ page import="com.se1020.vehiclerental.dao.CustomerDAO" %>
<%@ page import="com.se1020.vehiclerental.model.Vehicle" %>
<%@ page import="com.se1020.vehiclerental.model.Customer" %>
<%@ page import="com.se1020.vehiclerental.model.Rental" %>
<%@ page import="java.util.List" %>
<%@ include file="/common/header.jsp" %>

<%
    List<Rental> rentals = (List<Rental>) request.getAttribute("rentals");
    VehicleDAO vehicleDAO = (VehicleDAO) request.getAttribute("vehicleDAO");
    CustomerDAO customerDAO = (CustomerDAO) request.getAttribute("customerDAO");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Rentals</h2>
    <a href="rentals?action=new" class="btn btn-success">+ New Rental</a>
</div>

<% if (errorMessage != null) { %>
    <div class="alert alert-danger"><%= errorMessage %></div>
<% } %>

<div class="card p-3">
    <table class="table table-hover align-middle mb-0">
        <thead>
        <tr>
            <th>ID</th>
            <th>Vehicle</th>
            <th>Customer</th>
            <th>Start</th>
            <th>Expected Return</th>
            <th>Actual Return</th>
            <th>Total Cost (LKR)</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <% if (rentals.isEmpty()) { %>
            <tr><td colspan="9" class="text-center text-muted py-4">No rentals yet.</td></tr>
        <% }
           for (Rental rental : rentals) {
               Vehicle v = vehicleDAO.findById(rental.getVehicleId());
               Customer c = customerDAO.findById(rental.getCustomerId());
               String vehicleLabel = (v != null) ? v.getBrand() + " " + v.getModel() + " (" + v.getRegistrationNumber() + ")" : rental.getVehicleId();
               String customerLabel = (c != null) ? c.getFullName() : rental.getCustomerId();
        %>
            <tr>
                <td><%= rental.getRentalId() %></td>
                <td><%= vehicleLabel %></td>
                <td><%= customerLabel %></td>
                <td><%= rental.getStartDate() %></td>
                <td><%= rental.getExpectedReturnDate() %></td>
                <td><%= rental.getActualReturnDate() != null ? rental.getActualReturnDate() : "-" %></td>
                <td><%= String.format("%,.2f", rental.getTotalCost()) %></td>
                <td><span class="badge badge-<%= rental.getStatus().toLowerCase() %>"><%= rental.getStatus() %></span></td>
                <td>
                    <% if ("ACTIVE".equals(rental.getStatus())) { %>
                        <a href="rentals?action=return&id=<%= rental.getRentalId() %>"
                           class="btn btn-sm btn-outline-success"
                           onclick="return confirm('Mark this vehicle as returned today?');">Return</a>
                        <a href="rentals?action=delete&id=<%= rental.getRentalId() %>"
                           class="btn btn-sm btn-outline-danger"
                           onclick="return confirm('Cancel this rental?');">Cancel</a>
                    <% } else { %>
                        <span class="text-muted">-</span>
                    <% } %>
                </td>
            </tr>
        <% } %>
        </tbody>
    </table>
</div>

<%@ include file="/common/footer.jsp" %>
