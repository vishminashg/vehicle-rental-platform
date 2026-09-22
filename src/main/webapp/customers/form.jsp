<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.se1020.vehiclerental.model.Customer" %>
<%@ page import="com.se1020.vehiclerental.model.PremiumCustomer" %>
<%@ include file="/common/header.jsp" %>

<%
    Customer customer = (Customer) request.getAttribute("customer");
    boolean isEdit = customer != null;
    String currentType = isEdit ? customer.getCustomerType().toUpperCase() : "REGULAR";
    String loyaltyValue = (customer instanceof PremiumCustomer)
            ? String.valueOf(((PremiumCustomer) customer).getLoyaltyPoints()) : "0";
%>

<h2 class="mb-4"><%= isEdit ? "Edit Customer" : "Register Customer" %></h2>

<div class="card p-4" style="max-width: 640px;">
    <form action="customers" method="post">
        <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
        <% if (isEdit) { %>
            <input type="hidden" name="customerId" value="<%= customer.getCustomerId() %>">
        <% } %>

        <div class="mb-3">
            <label class="form-label">Customer Type</label>
            <select name="type" id="typeSelect" class="form-select" onchange="toggleLoyalty()" required>
                <option value="REGULAR" <%= currentType.equals("REGULAR") ? "selected" : "" %>>Regular</option>
                <option value="PREMIUM" <%= currentType.equals("PREMIUM") ? "selected" : "" %>>Premium (15% discount)</option>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Full Name</label>
            <input type="text" name="fullName" class="form-control"
                   value="<%= isEdit ? customer.getFullName() : "" %>" required>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Email</label>
                <input type="email" name="email" class="form-control"
                       value="<%= isEdit ? customer.getEmail() : "" %>" required>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Phone Number</label>
                <input type="text" name="phoneNumber" class="form-control"
                       value="<%= isEdit ? customer.getPhoneNumber() : "" %>" required>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Driving License Number</label>
            <input type="text" name="licenseNumber" class="form-control"
                   value="<%= isEdit ? customer.getLicenseNumber() : "" %>" required>
        </div>

        <div class="mb-3" id="loyaltyGroup">
            <label class="form-label">Loyalty Points</label>
            <input type="number" step="0.01" min="0" name="loyaltyPoints" class="form-control"
                   value="<%= loyaltyValue %>">
        </div>

        <button type="submit" class="btn btn-primary">Save Customer</button>
        <a href="customers" class="btn btn-outline-secondary">Cancel</a>
    </form>
</div>

<script>
    function toggleLoyalty() {
        const type = document.getElementById('typeSelect').value;
        document.getElementById('loyaltyGroup').style.display = (type === 'PREMIUM') ? 'block' : 'none';
    }
    toggleLoyalty();
</script>

<%@ include file="/common/footer.jsp" %>
