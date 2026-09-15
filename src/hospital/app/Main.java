package hospital.app;

import hospital.exception.DoctorUnavailableException;
import hospital.exception.InvalidAppointmentException;
import hospital.model.Appointment;
import hospital.model.Doctor;
import hospital.model.Person;
import hospital.model.Patient;
import hospital.model.Specialty;
import hospital.service.HospitalService;
import hospital.util.FileUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        HospitalService service = new HospitalService();

        Path seedPath = Path.of(
                "data",
                "seed-data.txt"
        );

        Path reportPath = Path.of(
                "reports",
                "appointment_report.txt"
        );

        System.out.println("======================================");
        System.out.println("HOSPITAL APPOINTMENT MANAGEMENT SYSTEM");
        System.out.println("======================================");

        // --------------------------------------------------
        // 1. LOAD SEED DATA
        // --------------------------------------------------

        try {

            loadSeedData(seedPath, service);

            System.out.println("\nSeed data loaded successfully.");

        } catch (IOException e) {

            System.out.println(
                    "Failed to load seed data: "
                            + e.getMessage()
            );

            return;

        } catch (RuntimeException e) {

            System.out.println(
                    "Invalid seed data: "
                            + e.getMessage()
            );

            return;
        }

        // --------------------------------------------------
        // 2. SHOW REGISTERED PEOPLE
        // --------------------------------------------------

        demonstratePeople(service);

        List<Patient> patients =
                service.getPatients();

        List<Doctor> doctors =
                service.getDoctors();

        Patient patient1 = patients.get(0);
        Patient patient2 = patients.get(1);
        Patient patient3 = patients.get(2);

        Doctor doctor1 = doctors.get(0);
        Doctor doctor2 = doctors.get(1);

        // --------------------------------------------------
        // 3. PREPARE FUTURE APPOINTMENT TIMES
        // --------------------------------------------------

        LocalDate appointmentDate =
                LocalDate.now().plusDays(1);

        LocalTime firstTime =
                LocalTime.of(10, 0);

        LocalTime secondTime =
                LocalTime.of(11, 0);

        LocalDateTime appointmentTime1 =
                LocalDateTime.of(
                        appointmentDate,
                        firstTime
                );

        LocalDateTime appointmentTime2 =
                LocalDateTime.of(
                        appointmentDate,
                        secondTime
                );

        // --------------------------------------------------
        // 4. SUCCESSFUL APPOINTMENTS
        // --------------------------------------------------

        System.out.println("\n--- SUCCESSFUL APPOINTMENTS ---");

        Appointment appointment1 = null;
        Appointment appointment2 = null;
        Appointment appointment3 = null;

        try {

            appointment1 =
                    service.scheduleAppointment(
                            patient1,
                            doctor1,
                            appointmentTime1
                    );

            System.out.println(
                    "Scheduled: " + appointment1
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "Could not schedule appointment 1: "
                            + e.getMessage()
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "Could not schedule appointment 1: "
                            + e.getMessage()
            );
        }

        try {

            appointment2 =
                    service.scheduleAppointment(
                            patient2,
                            doctor2,
                            appointmentTime1
                    );

            System.out.println(
                    "Scheduled: " + appointment2
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "Could not schedule appointment 2: "
                            + e.getMessage()
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "Could not schedule appointment 2: "
                            + e.getMessage()
            );
        }

        try {

            appointment3 =
                    service.scheduleAppointment(
                            patient3,
                            doctor1,
                            appointmentDate,
                            secondTime
                    );

            System.out.println(
                    "Scheduled using overloaded method: "
                            + appointment3
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "Could not schedule appointment 3: "
                            + e.getMessage()
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "Could not schedule appointment 3: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------
        // 5. CONFLICT TEST
        // --------------------------------------------------

        System.out.println("\n--- CONFLICT TEST ---");

        try {

            service.scheduleAppointment(
                    patient3,
                    doctor1,
                    appointmentTime1
            );

            System.out.println(
                    "ERROR: Conflict was not prevented."
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "Conflict correctly prevented: "
                            + e.getMessage()
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "Unexpected invalid appointment: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------
        // 6. INVALID PAST APPOINTMENT TEST
        // --------------------------------------------------

        System.out.println(
                "\n--- INVALID APPOINTMENT TEST ---"
        );

        try {

            LocalDateTime pastTime =
                    LocalDateTime.now().minusDays(1);

            service.scheduleAppointment(
                    patient3,
                    doctor2,
                    pastTime
            );

            System.out.println(
                    "ERROR: Past appointment was accepted."
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "Invalid appointment correctly prevented: "
                            + e.getMessage()
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "Unexpected doctor conflict: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------
        // 7. NULL PATIENT TEST
        // --------------------------------------------------

        System.out.println(
                "\n--- NULL PATIENT TEST ---"
        );

        try {

            service.scheduleAppointment(
                    null,
                    doctor2,
                    appointmentTime2
            );

            System.out.println(
                    "ERROR: Null patient was accepted."
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "Null patient correctly prevented: "
                            + e.getMessage()
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "Unexpected doctor conflict: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------
        // 8. COMPLETE APPOINTMENT
        // --------------------------------------------------

        System.out.println(
                "\n--- APPOINTMENT COMPLETION ---"
        );

        if (appointment1 != null) {

            try {

                service.completeAppointment(
                        appointment1.getAppointmentId()
                );

                System.out.println(
                        "Completed: "
                                + appointment1.getAppointmentId()
                );

                System.out.println(
                        appointment1
                );

            } catch (InvalidAppointmentException e) {

                System.out.println(
                        "Could not complete appointment: "
                                + e.getMessage()
                );
            }
        }

        // --------------------------------------------------
        // 9. CANCEL APPOINTMENT
        // --------------------------------------------------

        System.out.println(
                "\n--- APPOINTMENT CANCELLATION ---"
        );

        if (appointment3 != null) {

            try {

                service.cancelAppointment(
                        appointment3.getAppointmentId()
                );

                System.out.println(
                        "Cancelled: "
                                + appointment3.getAppointmentId()
                );

                System.out.println(
                        appointment3
                );

            } catch (InvalidAppointmentException e) {

                System.out.println(
                        "Could not cancel appointment: "
                                + e.getMessage()
                );
            }
        }

        // --------------------------------------------------
        // 10. INVALID STATE TRANSITION
        // --------------------------------------------------

        System.out.println(
                "\n--- INVALID STATE TRANSITION ---"
        );

        if (appointment1 != null) {

            try {

                service.cancelAppointment(
                        appointment1.getAppointmentId()
                );

                System.out.println(
                        "ERROR: Completed appointment was cancelled."
                );

            } catch (InvalidAppointmentException e) {

                System.out.println(
                        "Invalid state transition prevented: "
                                + e.getMessage()
                );
            }
        }

        // --------------------------------------------------
        // 11. ADDITIONAL BUSINESS RULE TESTS
        // --------------------------------------------------

        System.out.println(
                "\n--- ADDITIONAL BUSINESS RULE TESTS ---"
        );

        // --------------------------------------------------
        // 11.1 NULL DOCTOR
        // --------------------------------------------------

        try {

            service.scheduleAppointment(
                    patient1,
                    null,
                    appointmentTime2
            );

            System.out.println(
                    "FAIL: Null doctor was accepted."
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "PASS: Null doctor prevented: "
                            + e.getMessage()
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "FAIL: Unexpected doctor conflict: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------
        // 11.2 NULL DATE/TIME
        // --------------------------------------------------

        try {

            service.scheduleAppointment(
                    patient1,
                    doctor1,
                    null
            );

            System.out.println(
                    "FAIL: Null date/time was accepted."
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "PASS: Null date/time prevented: "
                            + e.getMessage()
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "FAIL: Unexpected doctor conflict: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------
        // 11.3 MISSING APPOINTMENT ID
        // --------------------------------------------------

        try {

            service.completeAppointment("A999");

            System.out.println(
                    "FAIL: Missing appointment was accepted."
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "PASS: Missing appointment prevented: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------
        // 11.4 CANCELLED SLOT CAN BE REUSED
        // --------------------------------------------------

        if (appointment3 != null) {

            try {

                Appointment replacementAppointment =
                        service.scheduleAppointment(
                                patient3,
                                doctor1,
                                appointmentDate,
                                secondTime
                        );

                System.out.println(
                        "PASS: Cancelled doctor's slot was reused: "
                                + replacementAppointment
                );

            } catch (InvalidAppointmentException e) {

                System.out.println(
                        "FAIL: Cancelled slot could not be reused: "
                                + e.getMessage()
                );

            } catch (DoctorUnavailableException e) {

                System.out.println(
                        "FAIL: Cancelled slot still appears occupied: "
                                + e.getMessage()
                );
            }
        }

        // --------------------------------------------------
        // 11.5 SCHEDULED SLOT REMAINS BLOCKED
        // --------------------------------------------------

        try {

            service.scheduleAppointment(
                    patient1,
                    doctor2,
                    appointmentTime1
            );

            System.out.println(
                    "FAIL: Scheduled doctor's slot was reused."
            );

        } catch (DoctorUnavailableException e) {

            System.out.println(
                    "PASS: Scheduled doctor's slot remains blocked: "
                            + e.getMessage()
            );

        } catch (InvalidAppointmentException e) {

            System.out.println(
                    "FAIL: Unexpected invalid appointment: "
                            + e.getMessage()
            );
        }

        // --------------------------------------------------
        // 11.6 COLLECTION PROTECTION
        // --------------------------------------------------

        try {

            service.getAppointments().clear();

            System.out.println(
                    "FAIL: Internal appointment collection was exposed."
            );

        } catch (UnsupportedOperationException e) {

            System.out.println(
                    "PASS: Appointment collection is protected."
            );
        }

        // --------------------------------------------------
        // 12. GENERATE REPORT
        // --------------------------------------------------

        System.out.println(
                "\n--- APPOINTMENT REPORT ---"
        );

        String report =
                service.generateReport();

        System.out.println(report);

        // --------------------------------------------------
        // 13. WRITE REPORT TO FILE
        // --------------------------------------------------

        try {

            FileUtil.writeText(
                    reportPath,
                    report
            );

            System.out.println(
                    "Report written successfully to: "
                            + reportPath
            );

        } catch (IOException e) {

            System.out.println(
                    "Failed to write report: "
                            + e.getMessage()
            );
        }
    }

    // ======================================================
    // SEED DATA LOADING
    // ======================================================

    private static void loadSeedData(
            Path path,
            HospitalService service
    ) throws IOException {

        List<String> lines =
                FileUtil.readLines(path);

        for (String line : lines) {

            if (line.isBlank()) {
                continue;
            }

            parseAndRegister(
                    line,
                    service
            );
        }
    }

    // ======================================================
    // SEED RECORD PARSING
    // ======================================================

    private static void parseAndRegister(
            String line,
            HospitalService service
    ) {

        String[] parts =
                line.split(",", -1);

        String type =
                parts[0]
                        .trim()
                        .toUpperCase();

        switch (type) {

            case "PATIENT":

                if (parts.length != 5) {

                    throw new IllegalArgumentException(
                            "Invalid patient record: "
                                    + line
                    );
                }

                Patient patient =
                        new Patient(
                                parts[1].trim(),
                                parts[2].trim(),
                                parts[3].trim(),
                                parts[4].trim()
                        );

                service.registerPatient(patient);

                break;

            case "DOCTOR":

                if (parts.length != 7) {

                    throw new IllegalArgumentException(
                            "Invalid doctor record: "
                                    + line
                    );
                }

                Specialty specialty =
                        Specialty.valueOf(
                                parts[5]
                                        .trim()
                                        .toUpperCase()
                        );

                double fee =
                        Double.parseDouble(
                                parts[6].trim()
                        );

                Doctor doctor =
                        new Doctor(
                                parts[1].trim(),
                                parts[2].trim(),
                                parts[3].trim(),
                                parts[4].trim(),
                                specialty,
                                fee
                        );

                service.registerDoctor(doctor);

                break;

            default:

                throw new IllegalArgumentException(
                        "Unknown record type: "
                                + type
                );
        }
    }

    // ======================================================
    // RUNTIME POLYMORPHISM DEMONSTRATION
    // ======================================================

    private static void demonstratePeople(
            HospitalService service
    ) {

        System.out.println(
                "\n--- REGISTERED PEOPLE ---"
        );

        System.out.println(
                "Patients: "
                        + service.getPatients().size()
        );

        System.out.println(
                "Doctors: "
                        + service.getDoctors().size()
        );

        ArrayList<Person> people =
                new ArrayList<>();

        people.addAll(
                service.getPatients()
        );

        people.addAll(
                service.getDoctors()
        );

        System.out.println(
                "\n--- RUNTIME POLYMORPHISM ---"
        );

        for (Person person : people) {

            person.displayRoleInformation();
        }
    }
}
