# Vehicle Rental Service Platform

SE1020 Object Oriented Programming - Individual Project (Prorata)
Student ID: IT22923042

A JSP and Servlet web application that manages vehicles, customers and rentals for a vehicle rental company. Data is stored in plain text files, so no database is needed.

## Features

- **Vehicles:** add, list, edit and delete cars, motorcycles and vans.
- **Customers:** register, list, edit and delete regular and premium customers.
- **Rentals:** create a rental, return a vehicle (late fee applied), cancel a rental.
- **Dashboard:** shows the number of vehicles, customers and rentals.
- **Validation:** invalid input (for example a negative daily rate or an email without `@`) shows an error message instead of crashing.

## Tech stack

| Layer | Choice |
|---|---|
| Language | Java 11 or higher (tested on JDK 21) |
| Web layer | Servlets (`javax.servlet`) on Apache Tomcat 9 |
| View layer | JSP and JSTL, Bootstrap 5 loaded from a CDN |
| Storage | Plain text files read and written with `java.io` |
| Build tool | Maven |

Tomcat 9 is required. Tomcat 10 and newer use the `jakarta.servlet` package and this code will not compile on them without changing the imports.

## Project structure

```
vehicle-rental-platform/
├── pom.xml
├── docs/
│   └── class-diagram.md
├── src/main/java/com/se1020/vehiclerental/
│   ├── model/
│   │   ├── Vehicle.java            (abstract)
│   │   ├── Car.java, Motorcycle.java, Van.java
│   │   ├── Customer.java           (abstract)
│   │   ├── RegularCustomer.java, PremiumCustomer.java
│   │   └── Rental.java
│   ├── dao/
│   │   ├── VehicleDAO.java
│   │   ├── CustomerDAO.java
│   │   └── RentalDAO.java
│   └── servlet/
│       ├── VehicleServlet.java     (/vehicles)
│       ├── CustomerServlet.java    (/customers)
│       └── RentalServlet.java      (/rentals)
└── src/main/webapp/
    ├── index.jsp
    ├── common/header.jsp, footer.jsp
    ├── css/style.css
    ├── vehicles/list.jsp, form.jsp
    ├── customers/list.jsp, form.jsp
    ├── rentals/list.jsp, form.jsp
    └── WEB-INF/
        ├── web.xml
        └── data/vehicles.txt, customers.txt, rentals.txt
```

## How to run (IntelliJ IDEA)

1. Open the folder that contains `pom.xml` (File, Open). Let Maven download the dependencies.
2. Set the project SDK to JDK 11 or higher (File, Project Structure).
3. Go to Run, Edit Configurations, add a **Tomcat Server, Local** configuration and point it to a Tomcat 9 installation.
4. On the Deployment tab, add the artifact `vehicle-rental-platform:war exploded` and set the application context to `/`.
5. If Tomcat reports that a port is already in use, stop the old Tomcat instance or change the HTTP port or JMX port in the configuration.
6. Run the configuration and open `http://localhost:8080/`.

Pages: `/vehicles`, `/customers`, `/rentals`.

## Architecture

The application follows MVC with the DAO pattern.

```
Browser -> Servlet (controller) -> DAO -> .txt file
                |
                +-> forwards to a JSP (view)
```

- **Model:** classes in `model` hold the data and business rules.
- **DAO:** classes in `dao` are the only code that reads or writes files.
- **Controller:** servlets read the request, call the DAOs and forward to a JSP or redirect.
- **View:** JSP pages in `webapp` display the data.

## Object oriented design

| Concept | Where it is used |
|---|---|
| Abstraction | `Vehicle` and `Customer` are abstract classes with abstract methods such as `calculateRentalCost()`, `getVehicleType()`, `getExtraDetails()`, `toFileString()` and `getDiscountRate()` |
| Inheritance | `Car`, `Motorcycle` and `Van` extend `Vehicle`. `RegularCustomer` and `PremiumCustomer` extend `Customer` |
| Polymorphism | `RentalServlet` calls `vehicle.calculateRentalCost(days)` and `customer.getDiscountRate()` on the parent type, and the subclass version runs at runtime |
| Encapsulation | All model fields are private and accessed through getters and setters |
| Information hiding | Setters validate input (for example `Customer.setEmail()` and `Vehicle.setBaseDailyRate()`), and only the DAO classes know the file format |

The class diagram is in [docs/class-diagram.md](docs/class-diagram.md).

## Business rules

| Rule | Value |
|---|---|
| Car cost per rental | (base daily rate + 500) x number of days |
| Motorcycle cost per rental | base daily rate x number of days |
| Van cost per rental | (base daily rate + 300) x number of days |
| Regular customer discount | 0% |
| Premium customer discount | 15% |
| Late return fee | 750 per day after the expected return date |
| Vehicle status | AVAILABLE, RENTED or MAINTENANCE |

When a rental is created the vehicle becomes RENTED. When the rental is returned or cancelled the vehicle becomes AVAILABLE again.

## File handling

Each DAO reads and writes files in `WEB-INF/data/`.

- **Read:** `BufferedReader` and `readLine()`, one record per line.
- **Add:** `FileWriter` in append mode.
- **Update and delete:** the whole file is read into a list, the list is changed in memory, and the file is rewritten.

File formats (fields are separated by the pipe character):

```
vehicles.txt   id|TYPE|brand|model|regNumber|baseDailyRate|status|extraField
customers.txt  id|TYPE|fullName|email|phone|licenseNumber[|loyaltyPoints]
rentals.txt    id|vehicleId|customerId|startDate|expectedReturnDate|actualReturnDate|totalCost|status
```

## Known limitations

- Delete actions use GET links. POST would be safer.
- File access is not synchronised, so simultaneous writes from several users could conflict.
- The data files are stored in the deployed web folder, so a clean rebuild can reset the data.
- Rentals do not check for overlapping dates for the same vehicle. Availability relies on the vehicle status.
- Loyalty points are stored but are not updated after a rental.
- A pipe character inside a text field would break the file format.
- There is no login or user roles.

## Possible improvements

- Search and filter on each list page.
- Admin login before delete actions.
- Move storage to a database.
- Export rental history to CSV or PDF.
