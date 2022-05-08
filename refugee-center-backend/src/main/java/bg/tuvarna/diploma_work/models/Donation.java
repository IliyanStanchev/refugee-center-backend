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

    private double quantity;

    private Unit unit;

    private String donatorName;

    private String donatorEmail;

    public Donation() {

    }

    public Donation(DonationType donationType, double quantity, Unit unit, String donatorName, String donatorEmail, String donatorPhoneNumber) {
        this.donationType = donationType;
        this.quantity = quantity;
        this.unit = unit;
        this.donatorName = donatorName;
        this.donatorEmail = donatorEmail;
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

    public String getDonatorName() {
        return donatorName;
    }

    public void setDonatorName(String donatorName) {
        this.donatorName = donatorName;
    }

    public String getDonatorEmail() {
        return donatorEmail;
    }

    public void setDonatorEmail(String donatorEmail) {
        this.donatorEmail = donatorEmail;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return Double.compare(donation.quantity, quantity) == 0 && Objects.equals(id, donation.id) && donationType == donation.donationType && unit == donation.unit && Objects.equals(donatorName, donation.donatorName) && Objects.equals(donatorEmail, donation.donatorEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, donationType, quantity, unit, donatorName, donatorEmail);
    }
}
