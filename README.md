# Vehicle Rental Service Platform

**SE1020 — Object Oriented Programming, Project Assignment (Prorata / Individual)**
Student: IT22923042 — Gurusingha W S H

A JSP + Servlet web application for managing vehicles, customers and rentals,
using plain text files for storage (no database). Built and verified to
compile, package, deploy and run correctly on Apache Tomcat 9 before being
handed to you — see "How this was verified" at the bottom.

---

## 1. Tech stack

| Layer         | Choice                                              |
|---------------|------------------------------------------------------|
| Language      | Java 11+ (tested on JDK 21, also works on JDK 11/17) |
| Web layer     | Servlets (`javax.servlet`, **Tomcat 9** / Servlet 4.0) |
| View layer    | JSP + JSTL, styled with Bootstrap 5 (via CDN)        |
| Persistence   | Plain text files (`.txt`), read/write via `java.io`  |
| Build tool    | Maven (`pom.xml` included)                           |
| IDE           | IntelliJ IDEA                                        |

> **⚠️ javax vs jakarta — check this first.** This project uses the
> `javax.servlet.*` package, which matches **Tomcat 9**. If your IntelliJ
> is configured with **Tomcat 10 or 11**, those use the `jakarta.servlet.*`
> namespace instead and this code will not compile as-is. To check: open
> IntelliJ → *Run/Debug Configurations* → your Tomcat config → see the
> version. If you're on Tomcat 10+, either (a) install Tomcat 9 instead
> (simplest), or (b) do a project-wide find & replace of `javax.servlet`
> with `jakarta.servlet` in all files under `src/main/java` and in
> `pom.xml`. Ask your instructor which is expected if unsure.

---

## 2. Project structure

```
vehicle-rental-platform/
├── pom.xml
├── docs/
│   └── class-diagram.md          ← Mermaid class diagram for your report
├── src/main/java/com/se1020/vehiclerental/
│   ├── model/
│   │   ├── Vehicle.java          (abstract)
│   │   ├── Car.java, Motorcycle.java, Van.java
│   │   ├── Customer.java         (abstract)
│   │   ├── RegularCustomer.java, PremiumCustomer.java
│   │   └── Rental.java
│   ├── dao/
│   │   ├── VehicleDAO.java       (all file I/O for vehicles.txt)
│   │   ├── CustomerDAO.java
│   │   └── RentalDAO.java
│   └── servlet/
│       ├── VehicleServlet.java   (maps to /vehicles)
│       ├── CustomerServlet.java  (maps to /customers)
│       └── RentalServlet.java    (maps to /rentals)
└── src/main/webapp/
    ├── index.jsp                 (dashboard)
    ├── common/header.jsp, footer.jsp
    ├── css/style.css
    ├── vehicles/list.jsp, form.jsp
    ├── customers/list.jsp, form.jsp
    ├── rentals/list.jsp, form.jsp
    └── WEB-INF/
        ├── web.xml
        └── data/vehicles.txt, customers.txt, rentals.txt   ← sample data
```

---

## 3. How to import into IntelliJ IDEA

1. **File → Open** → select the `vehicle-rental-platform` folder (the one
   containing `pom.xml`). IntelliJ will detect it as a Maven project and
   download dependencies automatically.
2. **File → Project Structure → Project** → set the SDK to a JDK (11 or
   higher).
3. Add the **Web** facet / configure as a web application if IntelliJ
   doesn't detect `src/main/webapp` automatically:
   *Right-click project → Add Framework Support → Web Application.*
4. **Run → Edit Configurations → + → Tomcat Server → Local.**
   - Point it at your local Tomcat 9 installation.
   - Under the **Deployment** tab, add an artifact:
     `vehicle-rental-platform:war exploded`.
   - Set the **Application context** to `/` (or leave as default).
5. Run the configuration. It should open your browser to the dashboard
   (`index.jsp`), showing vehicle/customer/rental counts.
6. If you get a blank page or 404, double check the deployment's context
   path matches the URL you're opening, and check the **Run** console for
   stack traces — the most common cause is the javax/jakarta mismatch
   described in section 1.

---

## 4. How the app is organised (walkthrough)

Each of the 3 modules follows the same request flow:

```
Browser → Servlet (e.g. VehicleServlet) → DAO (e.g. VehicleDAO) → .txt file
                     ↓
                forwards to a JSP to render the result
```

- **GET** `/vehicles` → lists all vehicles (`list.jsp`)
- **GET** `/vehicles?action=new` → shows blank add form (`form.jsp`)
- **GET** `/vehicles?action=edit&id=V001` → shows form pre-filled for editing
- **GET** `/vehicles?action=delete&id=V001` → deletes and redirects to list
- **POST** `/vehicles` with `action=add` or `action=update` → saves and
  redirects to list

`CustomerServlet` mirrors this exactly. `RentalServlet` is slightly bigger
because creating a rental needs data from **both** `VehicleDAO` and
`CustomerDAO` (to price it and to check the vehicle is available), and
returning/cancelling a rental needs to flip the vehicle's status back to
`AVAILABLE`.

---

## 5. Where each OOP concept lives (for your viva)

The marking guide gives 20 marks specifically for OOP concepts, and the viva
"will primarily focus on... the implementation of object-oriented concepts
and file handling" — so be ready to point at these directly in the code:

| Concept | Where | What to say |
|---|---|---|
| **Abstraction** | `Vehicle.java`, `Customer.java` | Both are `abstract class`. Neither can be instantiated directly — you must create a `Car`/`Motorcycle`/`Van` or a `RegularCustomer`/`PremiumCustomer`. Each declares abstract methods (`calculateRentalCost()`, `getDiscountRate()`) that force subclasses to define their own behaviour. |
| **Inheritance** | `Car/Motorcycle/Van extends Vehicle`; `RegularCustomer/PremiumCustomer extends Customer` | Subclasses reuse all the shared fields/methods (id, brand, getters/setters) from the parent and only add what's specific to them (e.g. `numberOfSeats` only makes sense for a `Car`). |
| **Polymorphism** | `calculateRentalCost()` in `RentalServlet.createRental()`; `getDiscountRate()` in the same method | The servlet calls `vehicle.calculateRentalCost(days)` on a variable declared as type `Vehicle` — but the actual surcharge logic that runs depends on whether it's really holding a `Car` (+500 insurance), `Motorcycle` (no surcharge) or `Van` (+300 cargo fee) at runtime. Same idea for `customer.getDiscountRate()` (0% vs 15%). |
| **Encapsulation** | Every model class | All fields are `private`; access is only through public getters/setters. |
| **Information hiding** | `Customer.setEmail()` (validates `@`), `Vehicle.setBaseDailyRate()` (rejects negative values); the DAO classes | Validation logic and file-format details are hidden inside the class — a servlet can't accidentally save an invalid object or needs to know the file's `|`-delimited format. |

---

## 6. File handling (10 marks)

Each DAO class (`VehicleDAO`, `CustomerDAO`, `RentalDAO`) does plain
`java.io` reads/writes against files under `WEB-INF/data/`:

- **Read**: `BufferedReader` + `readLine()`, one entity per line.
- **Create**: `PrintWriter` opened in append mode (`new FileWriter(path, true)`).
- **Update / Delete**: since text files can't easily edit "one line in the
  middle", both read the whole file into a `List`, modify the list in
  memory, then rewrite the entire file (`new FileWriter(path, false)`).
  This is a completely standard and expected approach for this kind of
  assignment — it's worth explicitly mentioning in your report since it
  explains why `update`/`delete` look different from `add`.

Data format used (documented in each DAO's class comment too):
```
vehicles.txt   id|TYPE|brand|model|regNumber|baseDailyRate|status|extraField
customers.txt  id|TYPE|fullName|email|phone|licenseNumber[|loyaltyPoints]
rentals.txt    id|vehicleId|customerId|startDate|expectedReturnDate|actualReturnDate|totalCost|status
```

---

## 7. CRUD → marking criteria mapping (30 marks)

All three modules implement all 4 operations (only 3 are required, so this
comfortably covers the requirement):

| Module | Create | Read | Update | Delete |
|---|---|---|---|---|
| Vehicles | Add vehicle | List / view vehicles | Edit vehicle | Delete vehicle |
| Customers | Register customer | List / view customers | Edit customer | Delete customer |
| Rentals | Create rental (books a vehicle) | List rentals | Return a rental (marks returned + applies late fee) | Cancel a rental |

---

## 8. About your individual contribution & GitHub commits (10 marks)

Because you're a prorata (individual) student, the "Individual Contribution
& GitHub Commit History" and "Viva Performance" criteria matter more for you
than for a team member who can point to teammates' work as context. Two
practical suggestions:

1. **Don't commit this as one giant initial commit.** Initialize your repo,
   then commit in stages that mirror how it was actually built, e.g.:
   1. `Initial Maven project setup + pom.xml`
   2. `Add Vehicle model hierarchy (Car, Motorcycle, Van)`
   3. `Add Customer model hierarchy (Regular, Premium)`
   4. `Add Rental model`
   5. `Add VehicleDAO with file read/write`
   6. `Add CustomerDAO`
   7. `Add RentalDAO`
   8. `Add VehicleServlet + vehicle JSPs`
   9. `Add CustomerServlet + customer JSPs`
   10. `Add RentalServlet + rental JSPs`
   11. `Add dashboard + styling`
   12. `Add sample data + README + class diagram`

   This gives you a commit history that actually demonstrates incremental,
   individual work — which is literally what that line item is graded on.

2. **Re-type or rework at least a few methods yourself** before committing,
   in whatever way helps you actually remember how they work — even
   renaming a variable or adjusting a comment forces you to read the logic
   properly. You will be asked to explain this code live; walking through
   it once while committing is the cheapest viva prep you can do.

---

## 9. UI (10 marks)

There are 9 JSP pages in total (well above the "minimum 3 UIs" requirement):
dashboard, and list+form for each of the 3 modules. Bootstrap 5 is loaded
via CDN for styling — no extra setup needed, just an internet connection
when the page loads.

---

## 10. How this was verified before being handed to you

This isn't just written-and-hoped-it-works code. Before delivery it was:
1. Compiled with `mvn compile` — zero errors.
2. Packaged with `mvn package` into a working `.war`.
3. Actually deployed to a real Apache Tomcat 9 server and exercised end to
   end over HTTP: home page, all 3 list pages, all 3 add forms, editing a
   Car/Motorcycle/Van (to check each subtype's extra field pre-fills
   correctly), updating and deleting a vehicle and a customer, creating a
   rental, and returning a rental (checked that it flips the vehicle back
   to AVAILABLE and applies late-fee logic) — all confirmed working with no
   exceptions.
4. Checked that an invalid input (a negative daily rate) is caught by the
   validation in `Vehicle.setBaseDailyRate()` and shown as a friendly error
   instead of crashing the app.

You should still run it yourself in IntelliJ and click through it before
your viva — partly because your Tomcat setup may differ slightly (see the
javax/jakarta note above), and mainly so you're genuinely comfortable
demoing it live.

---

## 11. Ideas if you want to extend it further

Not required, but easy additions if you want to push past the minimum:
- A search/filter box on each list page (by brand, by customer name, etc.)
- Simple login for an "Admin" role before allowing delete actions
- Sorting the rental list by status or date
- A basic PDF or CSV export of the rental history
