package bg.tuvarna.diploma_work.schedulers;

import bg.tuvarna.diploma_work.models.Donation;
import bg.tuvarna.diploma_work.models.DonationAbsorption;
import bg.tuvarna.diploma_work.services.DonationAbsorptionService;
import bg.tuvarna.diploma_work.services.DonationService;
import bg.tuvarna.diploma_work.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DonationAbsorptionScheduler {

    final static int DAYS_TILL_OUT_OF_STOCK_TO_NOTIFY = 7;

    @Autowired
    private DonationAbsorptionService donationAbsorptionService;

    @Autowired
    private DonationService donationService;

    @Autowired
    private MessageService messageService;

    @Scheduled(cron = "@daily")
    @Transactional
    public void absorbDonations() {

        List<Donation> donationList = donationService.getAllDonations();

        for( Donation donation : donationList ) {

            List<DonationAbsorption> absorptions = donationAbsorptionService.getAbsorptions( donation.getId() );

            double absorbed = 0;
            for( DonationAbsorption absorption : absorptions ) {

                absorbed += absorption.getAbsorption();
            }

            final double donationQuantity = donation.getQuantity();
            final double remaining = donationQuantity - absorbed;
            final int daysTillOutOfStock = (int) Math.ceil( remaining / absorbed );

            if( remaining <= 0 ) {

                messageService.notifyDonationOutOfStock(donation);

            }else if ( daysTillOutOfStock <= DAYS_TILL_OUT_OF_STOCK_TO_NOTIFY ) {

                messageService.notifyDonationOutOfStock( donation, daysTillOutOfStock);
            }

            donation.setQuantity( remaining < 0 ? 0 : remaining );
            donationService.saveDonation(donation);
        }
    }
}
