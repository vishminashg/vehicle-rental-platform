package com.se1020.vehiclerental.servlet;

import com.se1020.vehiclerental.dao.CustomerDAO;
import com.se1020.vehiclerental.model.Customer;
import com.se1020.vehiclerental.model.PremiumCustomer;
import com.se1020.vehiclerental.model.RegularCustomer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CustomerServlet", urlPatterns = {"/customers"})
public class CustomerServlet extends HttpServlet {

    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        String dataFilePath = getServletContext().getRealPath("/WEB-INF/data/customers.txt");
        customerDAO = new CustomerDAO(dataFilePath);
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
                Customer customer = customerDAO.findById(request.getParameter("id"));
                showForm(request, response, customer);
                break;
            case "delete":
                customerDAO.delete(request.getParameter("id"));
                response.sendRedirect("customers?action=list");
                break;
            case "list":
            default:
                listCustomers(request, response, null);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            Customer customer = buildCustomerFromRequest(request);

            if ("update".equals(action)) {
                customerDAO.update(customer);
            } else { // "add"
                customer.setCustomerId(customerDAO.generateNextId());
                customerDAO.add(customer);
            }
            response.sendRedirect("customers?action=list");

        } catch (IllegalArgumentException e) {
            listCustomers(request, response, "Could not save customer: " + e.getMessage());
        }
    }

    // ---------- helpers ----------

    private void listCustomers(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        List<Customer> customers = customerDAO.getAll();
        request.setAttribute("customers", customers);
        request.setAttribute("errorMessage", errorMessage);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/customers/list.jsp");
        dispatcher.forward(request, response);
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException {
        request.setAttribute("customer", customer);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/customers/form.jsp");
        dispatcher.forward(request, response);
    }

    private Customer buildCustomerFromRequest(HttpServletRequest request) {
        String id = request.getParameter("customerId");
        String type = request.getParameter("type");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String licenseNumber = request.getParameter("licenseNumber");

        if ("PREMIUM".equalsIgnoreCase(type)) {
            String pointsParam = request.getParameter("loyaltyPoints");
            double loyaltyPoints = (pointsParam == null || pointsParam.isEmpty())
                    ? 0 : Double.parseDouble(pointsParam);
            return new PremiumCustomer(id, fullName, email, phoneNumber, licenseNumber, loyaltyPoints);
        } else {
            return new RegularCustomer(id, fullName, email, phoneNumber, licenseNumber);
        }
    }
}
