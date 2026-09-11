package hospital.service;

import hospital.exception.DoctorUnavailableException;
import hospital.exception.InvalidAppointmentException;
import hospital.model.Appointment;
import hospital.model.AppointmentStatus;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.Reportable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HospitalService implements Reportable {

    private final ArrayList<Patient> patients;
    private final ArrayList<Doctor> doctors;
    private final ArrayList<Appointment> appointments;

    public HospitalService() {
        this.patients = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    public void registerPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException(
                    "Patient cannot be null."
            );
        }

        patients.add(patient);
    }

    public void registerDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException(
                    "Doctor cannot be null."
            );
        }

        doctors.add(doctor);
    }

    public Appointment scheduleAppointment(
            Patient patient,
            Doctor doctor,
            LocalDateTime dateTime
    ) throws InvalidAppointmentException,
             DoctorUnavailableException {

        validateAppointmentRequest(patient, doctor, dateTime);
        checkDoctorAvailability(doctor, dateTime);

        Appointment appointment =
                new Appointment(patient, doctor, dateTime);

        appointments.add(appointment);

        return appointment;
    }

    public Appointment scheduleAppointment(
            Patient patient,
            Doctor doctor,
            LocalDate date,
            LocalTime time
    ) throws InvalidAppointmentException,
             DoctorUnavailableException {

        if (date == null || time == null) {
            throw new InvalidAppointmentException(
                    "Date and time cannot be null."
            );
        }

        return scheduleAppointment(
                patient,
                doctor,
                LocalDateTime.of(date, time)
        );
    }

    private void validateAppointmentRequest(
            Patient patient,
            Doctor doctor,
            LocalDateTime dateTime
    ) throws InvalidAppointmentException {

        if (patient == null) {
            throw new InvalidAppointmentException(
                    "Patient cannot be null."
            );
        }

        if (doctor == null) {
            throw new InvalidAppointmentException(
                    "Doctor cannot be null."
            );
        }

        if (dateTime == null) {
            throw new InvalidAppointmentException(
                    "Appointment date and time cannot be null."
            );
        }

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException(
                    "Appointment cannot be scheduled in the past."
            );
        }
    }

    private void checkDoctorAvailability(
            Doctor doctor,
            LocalDateTime dateTime
    ) throws DoctorUnavailableException {

        for (Appointment appointment : appointments) {

            boolean sameDoctor =
                    appointment.getDoctor() == doctor;

            boolean sameDateTime =
                    appointment.getDateTime().equals(dateTime);

            boolean active =
                    appointment.getStatus()
                            == AppointmentStatus.SCHEDULED;

            if (sameDoctor && sameDateTime && active) {
                throw new DoctorUnavailableException(
                        "Doctor " + doctor.getName() +
                        " is already booked at " + dateTime
                );
            }
        }
    }

    public Appointment findAppointmentById(String appointmentId)
            throws InvalidAppointmentException {

        if (appointmentId == null || appointmentId.isBlank()) {
            throw new InvalidAppointmentException(
                    "Appointment ID cannot be empty."
            );
        }

        for (Appointment appointment : appointments) {
            if (appointment.getAppointmentId()
                    .equals(appointmentId)) {
                return appointment;
            }
        }

        throw new InvalidAppointmentException(
                "Appointment not found: " + appointmentId
        );
    }

    public void cancelAppointment(String appointmentId)
            throws InvalidAppointmentException {

        Appointment appointment =
                findAppointmentById(appointmentId);

        appointment.cancel();
    }

    public void completeAppointment(String appointmentId)
            throws InvalidAppointmentException {

        Appointment appointment =
                findAppointmentById(appointmentId);

        appointment.complete();
    }

    public List<Patient> getPatients() {
        return Collections.unmodifiableList(patients);
    }

    public List<Doctor> getDoctors() {
        return Collections.unmodifiableList(doctors);
    }

    public List<Appointment> getAppointments() {
        return Collections.unmodifiableList(appointments);
    }

    @Override
    public String generateReport() {

        StringBuilder report = new StringBuilder();

        report.append("===== HOSPITAL APPOINTMENT REPORT =====\n\n");

        for (Appointment appointment : appointments) {
            report.append(appointment).append("\n");
        }

        report.append("\n===== END OF REPORT =====\n");

        return report.toString();
    }
}