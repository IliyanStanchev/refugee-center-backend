package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.RequestStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "MEDICAL_HELP_REQUESTS")
public class MedicalHelpRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "refugee_id")
    private Refugee refugee;

    String description;

    LocalDateTime dateCreated;

    public MedicalHelpRequest() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Refugee getRefugee() {
        return refugee;
    }

    public void setRefugee(Refugee refugee) {
        this.refugee = refugee;
    }

    public RequestStatus getRequestStatus(){

        return RequestStatus.Approved;
    }
}