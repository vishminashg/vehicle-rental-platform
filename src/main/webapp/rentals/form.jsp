<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.se1020.vehiclerental.model.Vehicle" %>
<%@ page import="com.se1020.vehiclerental.model.Customer" %>
<%@ page import="java.util.List" %>
<%@ include file="/common/header.jsp" %>

<%
    List<Vehicle> availableVehicles = (List<Vehicle>) request.getAttribute("availableVehicles");
    List<Customer> customers = (List<Customer>) request.getAttribute("customers");
    String errorMessage = (String) request.getAttribute("errorMessage");
%>

<h2 class="mb-4">New Rental</h2>

<% if (errorMessage != null) { %>
    <div class="alert alert-danger"><%= errorMessage %></div>
<% } %>

<% if (availableVehicles.isEmpty()) { %>
    <div class="alert alert-warning">No vehicles are currently available to rent.</div>
<% } else if (customers.isEmpty()) { %>
    <div class="alert alert-warning">No customers registered yet - <a href="customers?action=new">add one first</a>.</div>
<% } else { %>

<div class="card p-4" style="max-width: 640px;">
    <form action="rentals" method="post">
        <div class="mb-3">
            <label class="form-label">Vehicle</label>
            <select name="vehicleId" class="form-select" required>
                <% for (Vehicle v : availableVehicles) { %>
                    <option value="<%= v.getVehicleId() %>">
                        <%= v.getVehicleType() %> - <%= v.getBrand() %> <%= v.getModel() %>
                        (<%= v.getRegistrationNumber() %>) - LKR <%= String.format("%,.2f", v.getBaseDailyRate()) %>/day
                    </option>
                <% } %>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Customer</label>
            <select name="customerId" class="form-select" required>
                <% for (Customer c : customers) { %>
                    <option value="<%= c.getCustomerId() %>">
                        <%= c.getFullName() %> (<%= c.getCustomerType() %>)
                    </option>
                <% } %>
            </select>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Start Date</label>
                <input type="date" name="startDate" class="form-control" required>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Expected Return Date</label>
                <input type="date" name="expectedReturnDate" class="form-control" required>
            </div>
        </div>

        <div class="form-text mb-3">
            The total cost is calculated automatically from the vehicle's daily rate
            (plus its type surcharge) and the customer's discount rate once you submit.
        </div>

        <button type="submit" class="btn btn-success">Create Rental</button>
        <a href="rentals" class="btn btn-outline-secondary">Cancel</a>
    </form>
</div>

<% } %>

<%@ include file="/common/footer.jsp" %>
