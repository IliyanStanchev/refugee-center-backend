package bg.tuvarna.diploma_work.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "DONATION_ABSORPTIONS")
public class DonationAbsorption implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @ManyToOne
    private Donation donation;

    @ManyToOne
    private Facility facility;

    @Column(nullable = false)
    private double absorption;

    public DonationAbsorption() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public double getAbsorption() {
        return absorption;
    }

    public void setAbsorption(double absorption) {
        this.absorption = absorption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonationAbsorption that = (DonationAbsorption) o;
        return Double.compare(that.absorption, absorption) == 0 && Objects.equals(id, that.id) && Objects.equals(donation, that.donation) && Objects.equals(facility, that.facility);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, donation, facility, absorption);
    }
}
