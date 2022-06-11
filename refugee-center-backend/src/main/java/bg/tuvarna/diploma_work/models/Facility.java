package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.FacilityType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "FACILITIES")
public class Facility implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne
    private User responsibleUser;

    @ManyToOne
    private Address address;

    private FacilityType facilityType;

    private long maxCapacity;

    private long currentCapacity;

    private String phoneNumber;

    public Facility() {
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(User responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public FacilityType getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    public long getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public long getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(long currentCapacity) {
        this.currentCapacity = currentCapacity;
    }

    public String getCapacity(){

        double capacity = (double) currentCapacity / maxCapacity;
        double percentage = capacity * 100;
        return percentage + "/100";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return Objects.equals(id, facility.id) && Objects.equals(responsibleUser, facility.responsibleUser) && facilityType == facility.facilityType ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, responsibleUser, facilityType);
    }

    public CharSequence getFacilityInformation() {

        return address.getCountryIsoCode() + ", " + address.getCityName() + ", " + address.getAddress();
    }
}
