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

    private Date lastInventoryDate;

    public Facility() {
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

    public Date getLastInventoryDate() {
        return lastInventoryDate;
    }

    public void setLastInventoryDate(Date lastInventoryDate) {
        this.lastInventoryDate = lastInventoryDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return Objects.equals(id, facility.id) && Objects.equals(responsibleUser, facility.responsibleUser) && facilityType == facility.facilityType && Objects.equals(lastInventoryDate, facility.lastInventoryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, responsibleUser, facilityType, lastInventoryDate);
    }
}
