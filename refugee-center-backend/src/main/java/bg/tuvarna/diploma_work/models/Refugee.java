package bg.tuvarna.diploma_work.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "REFUGEES")
public class Refugee implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne
    private Address address;

    private String phoneNumber;

    private Long age;

    @ManyToOne
    private Facility facility;

    @OneToOne
    private User user;

    private String diseases;

    private String allergens;

    private String specialDiet;

    public Refugee(){

    }

    public Refugee(String firstName, String lastName, String identifier, Long age, Address address) {
        this.age = age;
        this.address = address;
    }

    public String getDiseases() {
        return diseases;
    }

    public void setDiseases(String diseases) {
        this.diseases = diseases;
    }

    public String getAllergens() {
        return allergens;
    }

    public void setAllergens(String allergens) {
        this.allergens = allergens;
    }

    public String getSpecialDiet() {
        return specialDiet;
    }

    public void setSpecialDiet(String specialDiet) {
        this.specialDiet = specialDiet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Refugee refugee = (Refugee) o;
        return Objects.equals(id, refugee.id) && Objects.equals(address, refugee.address) && Objects.equals(phoneNumber, refugee.phoneNumber) && Objects.equals(age, refugee.age) && Objects.equals(facility, refugee.facility) && Objects.equals(user, refugee.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, phoneNumber, age, facility, user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {

        return user.getFirstName() + " " + user.getLastName();
    }
}
