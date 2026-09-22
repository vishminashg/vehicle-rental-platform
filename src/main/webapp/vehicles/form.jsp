<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.se1020.vehiclerental.model.Vehicle" %>
<%@ page import="com.se1020.vehiclerental.model.Car" %>
<%@ page import="com.se1020.vehiclerental.model.Motorcycle" %>
<%@ page import="com.se1020.vehiclerental.model.Van" %>
<%@ include file="/common/header.jsp" %>

<%
    // Scriptlet kept small and only used to pull out the type-specific extra
    // value when editing (EL alone can't cast an abstract Vehicle back down
    // to its concrete subclass to read a field that only that subclass has).
    Vehicle vehicle = (Vehicle) request.getAttribute("vehicle");
    boolean isEdit = vehicle != null;
    String currentType = isEdit ? vehicle.getVehicleType().toUpperCase() : "CAR";
    String extraValue = "";
    if (vehicle instanceof Car) {
        extraValue = String.valueOf(((Car) vehicle).getNumberOfSeats());
    } else if (vehicle instanceof Motorcycle) {
        extraValue = ((Motorcycle) vehicle).getEngineCapacity();
    } else if (vehicle instanceof Van) {
        extraValue = String.valueOf(((Van) vehicle).getCargoCapacityKg());
    }
%>

<h2 class="mb-4"><%= isEdit ? "Edit Vehicle" : "Add Vehicle" %></h2>

<div class="card p-4" style="max-width: 640px;">
    <form action="vehicles" method="post">
        <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
        <% if (isEdit) { %>
            <input type="hidden" name="vehicleId" value="<%= vehicle.getVehicleId() %>">
        <% } %>

        <div class="mb-3">
            <label class="form-label">Vehicle Type</label>
            <select name="type" id="typeSelect" class="form-select" onchange="updateExtraLabel()" required>
                <option value="CAR" <%= currentType.equals("CAR") ? "selected" : "" %>>Car</option>
                <option value="MOTORCYCLE" <%= currentType.equals("MOTORCYCLE") ? "selected" : "" %>>Motorcycle</option>
                <option value="VAN" <%= currentType.equals("VAN") ? "selected" : "" %>>Van</option>
            </select>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Brand</label>
                <input type="text" name="brand" class="form-control"
                       value="<%= isEdit ? vehicle.getBrand() : "" %>" required>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Model</label>
                <input type="text" name="model" class="form-control"
                       value="<%= isEdit ? vehicle.getModel() : "" %>" required>
            </div>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Registration Number</label>
                <input type="text" name="registrationNumber" class="form-control"
                       value="<%= isEdit ? vehicle.getRegistrationNumber() : "" %>" required>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Base Daily Rate (LKR)</label>
                <input type="number" step="0.01" min="0" name="baseDailyRate" class="form-control"
                       value="<%= isEdit ? vehicle.getBaseDailyRate() : "" %>" required>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label" id="extraLabel">Number of Seats</label>
            <input type="text" name="extraField" id="extraField" class="form-control"
                   value="<%= extraValue %>" required>
            <div class="form-text" id="extraHint">e.g. 4, 5, 7</div>
        </div>

        <div class="mb-3">
            <label class="form-label">Status</label>
            <select name="status" class="form-select">
                <option value="AVAILABLE" <%= isEdit && vehicle.getStatus().equals("AVAILABLE") ? "selected" : "" %>>Available</option>
                <option value="RENTED" <%= isEdit && vehicle.getStatus().equals("RENTED") ? "selected" : "" %>>Rented</option>
                <option value="MAINTENANCE" <%= isEdit && vehicle.getStatus().equals("MAINTENANCE") ? "selected" : "" %>>Maintenance</option>
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Save Vehicle</button>
        <a href="vehicles" class="btn btn-outline-secondary">Cancel</a>
    </form>
</div>

<script>
    function updateExtraLabel() {
        const type = document.getElementById('typeSelect').value;
        const label = document.getElementById('extraLabel');
        const hint = document.getElementById('extraHint');
        if (type === 'CAR') {
            label.textContent = 'Number of Seats';
            hint.textContent = 'e.g. 4, 5, 7';
        } else if (type === 'MOTORCYCLE') {
            label.textContent = 'Engine Capacity';
            hint.textContent = 'e.g. 125cc, 150cc';
        } else {
            label.textContent = 'Cargo Capacity (kg)';
            hint.textContent = 'e.g. 800, 1200';
        }
    }
    // Set the correct label on page load (important when editing)
    updateExtraLabel();
</script>

<%@ include file="/common/footer.jsp" %>
