package bg.tuvarna.diploma_work.storages;

import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.models.Donor;

import java.io.Serializable;

public class DonationData implements Serializable {

    private Donation donation;

    private Donor donor;

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }
}
