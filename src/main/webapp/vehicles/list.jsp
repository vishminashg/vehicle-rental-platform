<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Vehicles</h2>
    <a href="vehicles?action=new" class="btn btn-primary">+ Add Vehicle</a>
</div>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div class="card p-3">
    <table class="table table-hover align-middle mb-0">
        <thead>
        <tr>
            <th>ID</th>
            <th>Type</th>
            <th>Brand / Model</th>
            <th>Reg. Number</th>
            <th>Daily Rate (LKR)</th>
            <th>Details</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="vehicle" items="${vehicles}">
            <tr>
                <td>${vehicle.vehicleId}</td>
                <td>${vehicle.vehicleType}</td>
                <td>${vehicle.brand} ${vehicle.model}</td>
                <td>${vehicle.registrationNumber}</td>
                <td><fmt:formatNumber value="${vehicle.baseDailyRate}" pattern="#,##0.00"/></td>
                <td>${vehicle.extraDetails}</td>
                <td>
                    <span class="badge badge-${vehicle.status.toLowerCase()}">${vehicle.status}</span>
                </td>
                <td>
                    <a href="vehicles?action=edit&id=${vehicle.vehicleId}" class="btn btn-sm btn-outline-secondary">Edit</a>
                    <a href="vehicles?action=delete&id=${vehicle.vehicleId}"
                       class="btn btn-sm btn-outline-danger"
                       onclick="return confirm('Delete this vehicle?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty vehicles}">
            <tr><td colspan="8" class="text-center text-muted py-4">No vehicles yet.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/common/footer.jsp" %>
