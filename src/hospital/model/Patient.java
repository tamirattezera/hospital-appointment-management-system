package hospital.model;

public class Patient extends Person {

    public Patient(String id, String name, String phone, String email) {
        super(id, name, phone, email);
    }

    @Override
    public void displayRoleInformation() {
        System.out.println("Role: Patient | Name: " + getName());
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}