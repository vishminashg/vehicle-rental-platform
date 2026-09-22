<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Customers</h2>
    <a href="customers?action=new" class="btn btn-primary">+ Register Customer</a>
</div>

<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div class="card p-3">
    <table class="table table-hover align-middle mb-0">
        <thead>
        <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>License No.</th>
            <th>Type</th>
            <th>Loyalty</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="customer" items="${customers}">
            <tr>
                <td>${customer.customerId}</td>
                <td>${customer.fullName}</td>
                <td>${customer.email}</td>
                <td>${customer.phoneNumber}</td>
                <td>${customer.licenseNumber}</td>
                <td><span class="badge badge-${customer.customerType.toLowerCase()}">${customer.customerType}</span></td>
                <td>${customer.extraDetails}</td>
                <td>
                    <a href="customers?action=edit&id=${customer.customerId}" class="btn btn-sm btn-outline-secondary">Edit</a>
                    <a href="customers?action=delete&id=${customer.customerId}"
                       class="btn btn-sm btn-outline-danger"
                       onclick="return confirm('Delete this customer?');">Delete</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty customers}">
            <tr><td colspan="8" class="text-center text-muted py-4">No customers yet.</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<%@ include file="/common/footer.jsp" %>
