package hospital.model;

public class Doctor extends Person {

    private Specialty specialty;
    private double consultationFee;

    public Doctor(
            String id,
            String name,
            String phone,
            String email,
            Specialty specialty,
            double consultationFee
    ) {
        super(id, name, phone, email);

        if (specialty == null) {
            throw new IllegalArgumentException("Specialty cannot be null.");
        }

        if (consultationFee < 0) {
            throw new IllegalArgumentException(
                    "Consultation fee cannot be negative."
            );
        }

        this.specialty = specialty;
        this.consultationFee = consultationFee;
    }

    public Doctor(
            String id,
            String name,
            String phone,
            String email,
            Specialty specialty
    ) {
        this(id, name, phone, email, specialty, 500.0);
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    @Override
    public void displayRoleInformation() {
        System.out.println(
                "Role: Doctor | Name: " + getName() +
                " | Specialty: " + specialty
        );
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", specialty=" + specialty +
                ", consultationFee=" + consultationFee +
                '}';
    }
}