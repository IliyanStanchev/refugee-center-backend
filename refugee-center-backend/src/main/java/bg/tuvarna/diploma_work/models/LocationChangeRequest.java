package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.RequestStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOCATION_CHANGE_REQUESTS")
public class LocationChangeRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "refugee_id")
    private Refugee refugee;

    @ManyToOne
    @JoinColumn(name = "shelter_id")
    private Facility shelter;

    RequestStatus requestStatus;

    String description;

    LocalDateTime dateCreated;

    String employeeComment;

    public LocationChangeRequest() {
    }

    public String getEmployeeComment() {
        return employeeComment;
    }

    public void setEmployeeComment(String employeeComment) {
        this.employeeComment = employeeComment;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestType) {
        this.requestStatus = requestType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Facility getShelter() {
        return shelter;
    }

    public void setShelter(Facility shelter) {
        this.shelter = shelter;
    }


    public Refugee getRefugee() {
        return refugee;
    }

    public void setRefugee(Refugee refugee) {
        this.refugee = refugee;
    }


}