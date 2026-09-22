# Class Diagram — Vehicle Rental Service Platform

Paste the block below into the [Mermaid Live Editor](https://mermaid.live) to render
and export it as a PNG/SVG for your final report, or view it directly if your
Markdown viewer supports Mermaid (GitHub does, automatically).

```mermaid
classDiagram
    class Vehicle {
        <<abstract>>
        -String vehicleId
        -String brand
        -String model
        -String registrationNumber
        -double baseDailyRate
        -String status
        +calculateRentalCost(int days) double*
        +getVehicleType() String*
        +getExtraDetails() String*
        +toFileString() String*
        +isAvailable() boolean
    }

    class Car {
        -int numberOfSeats
        +calculateRentalCost(int days) double
        +getVehicleType() String
        +getExtraDetails() String
    }

    class Motorcycle {
        -String engineCapacity
        +calculateRentalCost(int days) double
        +getVehicleType() String
        +getExtraDetails() String
    }

    class Van {
        -double cargoCapacityKg
        +calculateRentalCost(int days) double
        +getVehicleType() String
        +getExtraDetails() String
    }

    Vehicle <|-- Car
    Vehicle <|-- Motorcycle
    Vehicle <|-- Van

    class Customer {
        <<abstract>>
        -String customerId
        -String fullName
        -String email
        -String phoneNumber
        -String licenseNumber
        +getDiscountRate() double*
        +getCustomerType() String*
        +getExtraDetails() String*
    }

    class RegularCustomer {
        +getDiscountRate() double
        +getCustomerType() String
    }

    class PremiumCustomer {
        -double loyaltyPoints
        +getDiscountRate() double
        +getCustomerType() String
    }

    Customer <|-- RegularCustomer
    Customer <|-- PremiumCustomer

    class Rental {
        -String rentalId
        -String vehicleId
        -String customerId
        -LocalDate startDate
        -LocalDate expectedReturnDate
        -LocalDate actualReturnDate
        -double totalCost
        -String status
        +calculateLateFee(LocalDate returnedOn) double
        +getPlannedDurationDays() long
    }

    Rental ..> Vehicle : rents (by vehicleId)
    Rental ..> Customer : booked by (by customerId)

    class VehicleDAO {
        -String filePath
        +getAll() List~Vehicle~
        +findById(String id) Vehicle
        +add(Vehicle v) void
        +update(Vehicle v) boolean
        +delete(String id) boolean
        +generateNextId() String
    }

    class CustomerDAO {
        -String filePath
        +getAll() List~Customer~
        +findById(String id) Customer
        +add(Customer c) void
        +update(Customer c) boolean
        +delete(String id) boolean
        +generateNextId() String
    }

    class RentalDAO {
        -String filePath
        +getAll() List~Rental~
        +findById(String id) Rental
        +add(Rental r) void
        +update(Rental r) boolean
        +delete(String id) boolean
        +generateNextId() String
    }

    VehicleDAO ..> Vehicle : reads/writes vehicles.txt
    CustomerDAO ..> Customer : reads/writes customers.txt
    RentalDAO ..> Rental : reads/writes rentals.txt

    class VehicleServlet
    class CustomerServlet
    class RentalServlet

    VehicleServlet --> VehicleDAO
    CustomerServlet --> CustomerDAO
    RentalServlet --> RentalDAO
    RentalServlet --> VehicleDAO
    RentalServlet --> CustomerDAO
```

## Quick explanation for the report / viva

- **Vehicle** and **Customer** are abstract superclasses — they can never be
  instantiated directly, only through a concrete subclass. This is
  **abstraction**.
- **Car, Motorcycle, Van** and **RegularCustomer, PremiumCustomer** each
  extend their abstract parent (**inheritance**) and provide their own
  version of the abstract methods (**polymorphism**) — e.g. calling
  `vehicle.calculateRentalCost(3)` runs different logic depending on whether
  `vehicle` actually holds a Car, Motorcycle or Van object at runtime.
- All fields in every model class are `private`, only reachable through
  public getters/setters, some of which validate input before accepting it
  (e.g. `Customer.setEmail()` rejects a string with no `@`). This is
  **encapsulation** and **information hiding**.
- The three **DAO** classes are the *only* classes that read or write the
  `.txt` files — every servlet goes through them instead of opening files
  itself, which is also information hiding, applied to the persistence layer.
- **Rental** is deliberately *not* part of either inheritance tree — it
  *has a* vehicle and a customer (composition by id reference), it *is not*
  a Vehicle or a Customer. This is a common point of confusion worth being
  able to explain in the viva.
