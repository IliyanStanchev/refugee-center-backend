package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.DonationType;
import bg.tuvarna.diploma_work.enumerables.Unit;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "DONATIONS")
public class Donation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    private DonationType donationType;

    private String name;

    private double quantity;

    private Unit unit;

    public Donation() {

    }

    public Donation(DonationType donationType, double quantity, Unit unit, String donatorName, String donatorEmail, String donatorPhoneNumber) {
        this.donationType = donationType;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DonationType getDonationType() {
        return donationType;
    }

    public void setDonationType(DonationType donationType) {
        this.donationType = donationType;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return Double.compare(donation.quantity, quantity) == 0 && Objects.equals(id, donation.id) && donationType == donation.donationType && unit == donation.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, donationType, quantity, unit );
    }
}
