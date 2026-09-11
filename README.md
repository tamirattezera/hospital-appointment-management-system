# 🏥 Hospital Appointment Management System

> A console-based Java application for managing patients, doctors, appointments, consultation fees, appointment conflicts, lifecycle states, and appointment reporting.

Built as an **Object-Oriented Programming engineering project** with a focus on translating real-world hospital requirements into a clean, maintainable Java domain model.

---

## 📌 Project Overview

The **Hospital Appointment Management System** manages the core workflow of a small hospital appointment environment:

- Register patients and doctors
- Represent different doctor specialties
- Schedule appointments
- Prevent conflicting doctor appointments
- Reject invalid appointment requests
- Calculate and preserve consultation fees
- Cancel and complete appointments
- Demonstrate runtime polymorphism
- Generate appointment reports
- Read initial data from a file
- Write generated reports to a text file
- Handle business failures using custom exceptions

The project intentionally uses **Java Standard Library only**.

There is no database, GUI, web framework, or external dependency.

The purpose is not to simulate an enterprise hospital platform, but to demonstrate strong **Java/OOP fundamentals, domain modeling, business-rule design, exception handling, collections, and file I/O** in a coherent system.

---

## 🎯 Engineering Goals

This project was built around the following progression:

```text
UNDERSTAND
    ↓
MODEL
    ↓
DESIGN
    ↓
IMPLEMENT
    ↓
TEST
    ↓
BREAK
    ↓
DEBUG
    ↓
EXPLAIN
    ↓
DOCUMENT
```

The goal was to avoid writing classes simply to satisfy an OOP checklist.

Each major Java/OOP feature exists because it solves a particular design problem.

---

# 🧠 Core Architecture

The system follows a simple layered structure:

```text
                         ┌─────────────────────┐
                         │       Main          │
                         │   Demo / Client     │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │  HospitalService    │
                         │ Business Operations │
                         └──────────┬──────────┘
                                    │
                 ┌──────────────────┼──────────────────┐
                 ▼                  ▼                  ▼
          ┌────────────┐     ┌────────────┐     ┌────────────┐
          │   Patient  │     │   Doctor   │     │ Appointment│
          └─────┬──────┘     └─────┬──────┘     └─────┬──────┘
                │                  │                    │
                └──────────────────┼────────────────────┘
                                   ▼
                              ┌──────────┐
                              │  Person  │
                              │ abstract │
                              └──────────┘

                  ┌──────────────────────────────┐
                  │          FileUtil            │
                  │      File I/O Boundary       │
                  └──────────────────────────────┘

                  ┌──────────────────────────────┐
                  │        Reportable            │
                  │        Interface             │
                  └──────────────────────────────┘
```

### Architectural principle

```text
Main
 ↓
HospitalService
 ↓
Domain Model
```

`Main` demonstrates the system.

`HospitalService` coordinates business operations.

Domain classes own their own state and behavior.

File handling is separated into `FileUtil`.

---

# 📁 Project Structure

```text
HospitalAppointmentSystem/
│
├── src/
│   └── hospital/
│       │
│       ├── app/
│       │   └── Main.java
│       │
│       ├── model/
│       │   ├── Person.java
│       │   ├── Patient.java
│       │   ├── Doctor.java
│       │   ├── Appointment.java
│       │   ├── Specialty.java
│       │   ├── AppointmentStatus.java
│       │   └── Reportable.java
│       │
│       ├── service/
│       │   └── HospitalService.java
│       │
│       ├── exception/
│       │   ├── DoctorUnavailableException.java
│       │   └── InvalidAppointmentException.java
│       │
│       └── util/
│           └── FileUtil.java
│
├── data/
│   └── seed-data.txt
│
├── reports/
│   └── appointment_report.txt
│
└── README.md
```

---

# 🧩 Domain Model

The core domain consists of four major entities.

```text
                    ┌────────────────┐
                    │     Person     │
                    │    abstract    │
                    └───────┬────────┘
                            │
                 ┌──────────┴──────────┐
                 │                     │
          ┌──────▼──────┐       ┌──────▼──────┐
          │   Patient   │       │   Doctor    │
          └─────────────┘       └──────┬───────┘
                                       │
                                  Specialty
                                       │
                                       ▼
                                ┌─────────────┐
                                │ Appointment │
                                └──────┬──────┘
                                       │
                                AppointmentStatus
```

### `Person`

Represents shared identity information.

```text
id
name
phone
email
```

It is abstract because the application works with concrete roles such as patients and doctors.

---

### `Patient`

Represents a hospital patient.

Inherits common identity information from `Person`.

---

### `Doctor`

Represents a doctor with:

```text
Specialty
consultationFee
```

Different doctors can therefore have different specialties and consultation fees.

---

### `Appointment`

Represents the relationship between a patient and doctor at a specific point in time.

```text
Appointment
├── appointmentId
├── patient
├── doctor
├── dateTime
├── consultationFee
└── status
```

The appointment captures the doctor's consultation fee at creation time.

This preserves the historical fee even if the doctor's current fee changes later.

---

# 🔄 Appointment Lifecycle

Appointments are modeled as a controlled state machine.

```text
                  ┌──────────────┐
                  │  SCHEDULED   │
                  └──────┬───────┘
                         │
                ┌────────┴────────┐
                ▼                 ▼
         ┌─────────────┐   ┌─────────────┐
         │  COMPLETED  │   │  CANCELLED  │
         └─────────────┘   └─────────────┘
```

The design intentionally avoids exposing an unrestricted:

```java
setStatus(...)
```

Instead, the appointment controls its own lifecycle through operations such as:

```text
cancel()
complete()
```

This protects the object's invariants.

For example:

```text
COMPLETED → CANCELLED
```

is rejected.

---

# ⚙️ Business Rules

The system enforces several domain rules.

## 1. Valid patient

An appointment cannot be created without a valid patient.

```text
patient == null
        ↓
InvalidAppointmentException
```

---

## 2. Valid doctor

An appointment cannot be created without a valid doctor.

```text
doctor == null
        ↓
InvalidAppointmentException
```

---

## 3. Valid date/time

The appointment must contain a valid date/time.

---

## 4. No past appointments

Appointments cannot be scheduled in the past.

```text
requestedDateTime < now
        ↓
InvalidAppointmentException
```

---

## 5. Doctor conflict prevention

A doctor cannot have two active appointments at the same date/time.

The conflict condition is:

```text
same doctor
    AND
same date/time
    AND
existing appointment is SCHEDULED
```

If the condition is true:

```text
DoctorUnavailableException
```

is thrown.

Cancelled and completed appointments do not block future scheduling.

---

## 6. Controlled appointment states

Only valid lifecycle transitions are allowed.

```text
SCHEDULED → COMPLETED
SCHEDULED → CANCELLED
```

Invalid transitions are rejected.

---

# 🚨 Exception Design

Two custom exceptions represent two different categories of failure.

### `InvalidAppointmentException`

Used when the appointment request or appointment state is invalid.

Examples:

```text
null patient
null doctor
null date/time
past appointment
unknown appointment
invalid state transition
```

### `DoctorUnavailableException`

Used when the request is valid but the requested doctor is already occupied.

This distinction makes error handling meaningful rather than returning generic `false` values.

---

# 🧱 OOP Design

The project deliberately demonstrates the major OOP concepts required by the assignment.

| OOP Concept          | Implementation                                       |
| -------------------- | ---------------------------------------------------- |
| Encapsulation        | Private fields + controlled behavior                 |
| Inheritance          | `Patient extends Person`, `Doctor extends Person`    |
| Abstraction          | Abstract `Person`                                    |
| Interface            | `Reportable`                                         |
| Runtime polymorphism | `Person` references to `Patient` / `Doctor`          |
| Method overriding    | `displayRoleInformation()`, `toString()`             |
| Method overloading   | Alternative `scheduleAppointment(...)` / constructor |
| `this`               | Constructor field initialization                     |
| `super`              | Subclass initialization of `Person`                  |
| Access modifiers     | `private`, `public`, `protected` where appropriate   |
| `static`             | Appointment ID generation/counter                    |
| `final`              | Meaningful constants                                 |
| Collections          | `ArrayList` for managed entities                     |
| Enum                 | `Specialty`, `AppointmentStatus`                     |
| Exception handling   | `try/catch`, `throw`, `throws`                       |
| Custom exceptions    | Two domain-specific exceptions                       |
| File I/O             | Seed data + appointment report                       |
| `toString()`         | Human-readable object representation                 |

---

# 🧬 Runtime Polymorphism

One of the central OOP demonstrations is treating different concrete objects through the common `Person` type.

Conceptually:

```text
ArrayList<Person>
       │
       ├── Patient
       ├── Doctor
       ├── Patient
       └── Doctor
```

The reference type is:

```text
Person
```

but the actual runtime objects are:

```text
Patient
Doctor
```

Calling:

```text
displayRoleInformation()
```

therefore executes the implementation belonging to the actual runtime object.

This demonstrates **dynamic method dispatch** rather than merely demonstrating inheritance syntax.

---

# 🔌 Interface Design

The system uses:

```text
Reportable
```

as a meaningful capability.

```text
Reportable
     │
     └── generateReport()
              ▲
              │
      HospitalService
```

The interface represents the capability of producing a report.

Scheduling was intentionally kept as a service responsibility because appointment scheduling requires coordination among multiple domain objects and business rules.

---

# 🗂️ Collection Ownership

`HospitalService` owns the application collections:

```text
private ArrayList<Patient>
private ArrayList<Doctor>
private ArrayList<Appointment>
```

The collections are not exposed as mutable internal state.

This protects the service's invariants.

For example, external code should not be able to bypass the scheduling rules by directly inserting an appointment into the appointment collection.

Instead:

```text
External code
      ↓
HospitalService
      ↓
validate
      ↓
check conflict
      ↓
create appointment
      ↓
store appointment
```

---

# 📄 File I/O Architecture

File operations are separated from business logic.

```text
seed-data.txt
      │
      ▼
   FileUtil
      │
      ▼
parse records
      │
      ▼
Patient / Doctor objects
      │
      ▼
HospitalService
```

For reports:

```text
HospitalService
      │
      ▼
generateReport()
      │
      ▼
report String
      │
      ▼
FileUtil
      │
      ▼
appointment_report.txt
```

`FileUtil` handles file mechanics.

`HospitalService` handles hospital business logic.

This prevents file-handling code from spreading throughout the domain model.

---

# 📊 Sample Seed Data

The system can load initial hospital data from:

```text
data/seed-data.txt
```

Example:

```text
PATIENT,P001,Abebe Kebede,0911000000,abebe@example.com
PATIENT,P002,Hana Tesfaye,0911000001,hana@example.com
PATIENT,P003,Dawit Alemu,0911000002,dawit@example.com

DOCTOR,D001,Dr. Sara,0912000000,sara@example.com,CARDIOLOGY,800
DOCTOR,D002,Dr. Daniel,0912000001,daniel@example.com,DERMATOLOGY,600
```

This provides:

- 3 patients
- 2 doctors
- multiple specialties
- different consultation fees

---

# 🧪 Demonstration & Testing

The application demonstrates both successful and failed operations.

### Positive scenarios

```text
✓ Register patient
✓ Register doctor
✓ Load seed data
✓ Schedule valid appointment
✓ Schedule different doctors at same time
✓ Complete appointment
✓ Cancel appointment
✓ Generate report
✓ Write report to file
✓ Runtime polymorphism
```

### Negative scenarios

```text
✗ Schedule with null patient
✗ Schedule with null doctor
✗ Schedule with null date/time
✗ Schedule appointment in the past
✗ Schedule conflicting doctor appointment
✗ Complete an already completed appointment
✗ Cancel an already cancelled/completed appointment
✗ Access unknown appointment
✗ Handle file I/O failure
```

The negative tests are important because they demonstrate that the system protects its business invariants rather than only working for the happy path.

---

# ▶️ Running the Application

## Requirements

- Java 21+
- Terminal / command line
- No external dependencies

Verify Java:

```bash
java -version
javac -version
```

---

## Compile

From the project root:

```bash
mkdir -p out

javac -d out $(find src -name "*.java")
```

---

## Run

```bash
java -cp out hospital.app.Main
```

The program should demonstrate:

```text
1. Loading hospital data
2. Displaying doctors and patients
3. Runtime polymorphism
4. Successful appointment scheduling
5. Conflict detection
6. Invalid appointment handling
7. Appointment cancellation/completion
8. Report generation
9. Report file creation
```

---

# 📄 Generated Report

The generated report is written to:

```text
reports/appointment_report.txt
```

A report contains information such as:

```text
Appointment ID
Patient
Doctor
Specialty
Date/Time
Consultation Fee
Status
```

---

# 🧭 Data Flow

The complete application flow can be summarized as:

```text
                 ┌─────────────────┐
                 │  seed-data.txt  │
                 └────────┬────────┘
                          │
                          ▼
                    ┌───────────┐
                    │ FileUtil  │
                    └─────┬─────┘
                          │
                          ▼
                 ┌─────────────────┐
                 │ Patient/Doctor  │
                 └────────┬────────┘
                          │
                          ▼
                 ┌─────────────────┐
                 │ HospitalService │
                 └────────┬────────┘
                          │
                    schedule()
                          │
                          ▼
                 ┌─────────────────┐
                 │   Appointment   │
                 └────────┬────────┘
                          │
                 lifecycle management
                          │
                          ▼
                 ┌─────────────────┐
                 │ generateReport()│
                 └────────┬────────┘
                          │
                          ▼
                 ┌──────────────────────┐
                 │ appointment_report   │
                 │       .txt           │
                 └──────────────────────┘
```

---

# 🏗️ Design Responsibilities

A major design principle is **placing behavior where it belongs**.

| Responsibility              | Owner               |
| --------------------------- | ------------------- |
| Common person identity      | `Person`            |
| Patient representation      | `Patient`           |
| Doctor specialization/fee   | `Doctor`            |
| Appointment state/lifecycle | `Appointment`       |
| Scheduling rules            | `HospitalService`   |
| Doctor conflict detection   | `HospitalService`   |
| Report generation           | `HospitalService`   |
| File reading/writing        | `FileUtil`          |
| Application demonstration   | `Main`              |
| Specialty values            | `Specialty`         |
| Appointment states          | `AppointmentStatus` |

This avoids turning `Main` into a "god class."

---

# 🧠 Important Design Decisions

## Why is `Person` abstract?

Because the system works with concrete hospital roles.

`Person` provides common state and behavior while forcing subclasses to define their specific role behavior.

---

## Why does `HospitalService` manage scheduling?

Scheduling requires coordination among:

```text
Patient
Doctor
Appointment
existing appointments
date/time
business rules
```

Therefore it is a system-level operation rather than something that belongs entirely to `Doctor` or `Patient`.

---

## Why does `Appointment` own `cancel()` and `complete()`?

Because an appointment owns its lifecycle state.

This prevents arbitrary external state mutation and keeps lifecycle rules close to the state they protect.

---

## Why use an enum?

Enums make domain states explicit and type-safe.

```text
AppointmentStatus.SCHEDULED
```

is safer than arbitrary strings.

---

## Why use custom exceptions?

Different failures have different meanings.

```text
Invalid request
        ≠
Doctor unavailable
```

Explicit exception types allow callers to respond appropriately.

---

## Why use `ArrayList`?

The assignment requires a collection-based solution, and the system is small enough that an in-memory `ArrayList` is appropriate.

A database-backed repository would be unnecessary complexity for this assignment.

---

# ⚖️ Assignment Version vs Production Version

This project intentionally represents an **academic but professionally designed Java application**.

A production hospital platform would require substantially more infrastructure.

### Current assignment architecture

```text
Console
  ↓
Service
  ↓
In-memory collections
  ↓
File storage
```

### Possible production evolution

```text
Web / Mobile Client
        ↓
REST API
        ↓
Authentication / Authorization
        ↓
Application Services
        ↓
Domain Model
        ↓
Database
        ↓
External Services
```

Potential production extensions include:

- PostgreSQL/MySQL persistence
- REST API
- authentication
- role-based access control
- doctor schedules
- appointment availability windows
- patient medical records
- notifications/reminders
- audit logging
- payment processing
- concurrency control
- transaction management
- automated testing
- observability and logging

These are intentionally **outside the scope of this assignment**.

---

# 🔬 Engineering Trade-offs

### Simplicity vs scalability

The application uses in-memory collections because the assignment does not require persistent database storage.

### Rich domain model vs unnecessary abstraction

The project uses meaningful domain classes but avoids unnecessary:

```text
Repository
DTO
Factory
Mapper
Controller
Dependency Injection Container
Configuration Layer
```

These abstractions would add complexity without solving a requirement in the current scope.

### File I/O vs database

File I/O demonstrates Java persistence concepts while keeping the project within the assignment's Java Standard Library constraint.

---

# 📚 Learning Outcomes

This project demonstrates practical understanding of:

### Java

- classes and objects
- constructors
- access modifiers
- `this`
- `super`
- `static`
- `final`
- collections
- enums
- exception handling
- file I/O
- method overloading
- method overriding

### Object-Oriented Design

- encapsulation
- inheritance
- abstraction
- polymorphism
- interface-based design
- responsibility assignment
- state management
- domain modeling
- invariant protection

### Engineering Thinking

- requirement → design mapping
- business-rule modeling
- failure modeling
- separation of concerns
- maintainability
- testable behavior
- design trade-offs
- production vs assignment scope

---

# 🧪 OOP Requirement Mapping

```text
Abstract Class
    → Person

Inheritance
    → Patient extends Person
    → Doctor extends Person

Encapsulation
    → private fields
    → controlled appointment lifecycle

Interface
    → Reportable

Runtime Polymorphism
    → Person references → Patient / Doctor

Overriding
    → displayRoleInformation()
    → toString()

Overloading
    → meaningful alternative constructors/method signatures

this
    → object state initialization

super
    → subclass constructor initialization

static
    → appointment ID generation

final
    → immutable constants

ArrayList
    → entity collections

Enum
    → Specialty / AppointmentStatus

Custom Exceptions
    → DoctorUnavailableException
    → InvalidAppointmentException

File Reading
    → seed-data.txt

File Writing
    → appointment_report.txt
```

---

# 🧑‍💻 Project Demonstration

The final demonstration follows a realistic sequence:

```text
LOAD DATA
    ↓
REGISTER / DISPLAY PEOPLE
    ↓
DEMONSTRATE POLYMORPHISM
    ↓
SCHEDULE VALID APPOINTMENT
    ↓
ATTEMPT CONFLICTING APPOINTMENT
    ↓
HANDLE EXCEPTION
    ↓
ATTEMPT INVALID APPOINTMENT
    ↓
HANDLE EXCEPTION
    ↓
COMPLETE / CANCEL APPOINTMENT
    ↓
GENERATE REPORT
    ↓
WRITE REPORT TO FILE
```

This sequence demonstrates both **successful behavior and defensive behavior**.

---

# 🚀 Future Evolution

If this project were expanded beyond the assignment, the architecture could evolve toward:

```text
                    Client Applications
                  /        |          \
                 /         |           \
             Web App    Mobile App    Admin
                 \         |           /
                  \        |          /
                   ▼       ▼         ▼
                     REST API
                        │
                        ▼
                Application Services
                        │
                        ▼
                   Domain Model
                        │
              ┌─────────┴─────────┐
              ▼                   ▼
          Database          External Services
```

Possible features:

```text
Doctor Availability
Patient Authentication
Appointment Reminders
Medical Records
Payment Integration
Admin Dashboard
Notifications
Audit Logs
Analytics
```

---

# 📈 Project Status

```text
[✓] Requirements analysis
[✓] Domain modeling
[✓] Architecture design
[✓] Class responsibility design
[✓] Business-rule design
[✓] Exception design
[✓] File I/O design
[✓] Implementation
[✓] Testing
[✓] Documentation
[ ] Production database
[ ] REST API
[ ] Authentication
[ ] GUI
```

The unchecked items are intentionally outside the assignment scope.

---

# 🎓 Academic Context

**Project:** Hospital Appointment Management System

**Language:** Java

**Application Type:** Console Application

**Architecture:** Simple layered/domain-oriented architecture

**Dependencies:** Java Standard Library

**Primary Focus:** Object-Oriented Programming

---

# 👤 Author

**Tamirat Tezera**

Software Engineering Student
AI-Powered Full-Stack Engineer in Progress

> **Understand the system. Build the system. Break the system. Debug the system. Explain the system.**

---

## ⭐ Key Takeaway

This project is more than a collection of Java classes.

It demonstrates the ability to take:

```text
Real-world requirements
        ↓
Domain concepts
        ↓
Object-oriented design
        ↓
Business rules
        ↓
Implementation
        ↓
Failure handling
        ↓
Testing
        ↓
Documentation
```

and turn them into a working software system.

That is the core engineering skill this project is intended to demonstrate.
