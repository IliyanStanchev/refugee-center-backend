package bg.tuvarna.diploma_work.models;

import javax.persistence.*;
import java.io.Serializable;

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

    public MedicalHelpRequest() {
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
}