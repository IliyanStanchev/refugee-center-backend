package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.RequestStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "STOCK_REQUESTS")
public class StockRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "refugee_id")
    private Refugee refugee;

    RequestStatus requestStatus;

    String description;

    String reason;

    public StockRequest() {
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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