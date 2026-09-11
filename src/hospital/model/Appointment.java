package hospital.model;

import hospital.exception.InvalidAppointmentException;

import java.time.LocalDateTime;

public class Appointment {

    private static int nextAppointmentId = 1;

    private final String appointmentId;
    private final Patient patient;
    private final Doctor doctor;
    private final LocalDateTime dateTime;
    private final double consultationFee;

    private AppointmentStatus status;

    public Appointment(
            Patient patient,
            Doctor doctor,
            LocalDateTime dateTime
    ) {
        this.appointmentId = String.format(
                "A%03d",
                nextAppointmentId++
        );

        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.consultationFee = doctor.getConsultationFee();
        this.status = AppointmentStatus.SCHEDULED;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void cancel() throws InvalidAppointmentException {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new InvalidAppointmentException(
                    "Only scheduled appointments can be cancelled."
            );
        }

        status = AppointmentStatus.CANCELLED;
    }

    public void complete() throws InvalidAppointmentException {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new InvalidAppointmentException(
                    "Only scheduled appointments can be completed."
            );
        }

        status = AppointmentStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId='" + appointmentId + '\'' +
                ", patient=" + patient.getName() +
                ", doctor=" + doctor.getName() +
                ", dateTime=" + dateTime +
                ", consultationFee=" + consultationFee +
                ", status=" + status +
                '}';
    }
}