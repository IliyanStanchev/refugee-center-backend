package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.models.User;
import bg.tuvarna.diploma_work.repositories.RefugeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RefugeeService {

    @Autowired
    private RefugeeRepository refugeeRepository;

    public Refugee getRefugeeByID(Long id) {

        Optional< Refugee > optionalRefugee = refugeeRepository.findById(id);

        if( optionalRefugee.isPresent() )
            return optionalRefugee.get();

        return null;
    }

    public Refugee saveRefugee(Refugee refugee) {

        return refugeeRepository.save(refugee);
    }

    public List<Refugee> getPendingRegistrations() {
        return refugeeRepository.getPendingRegistrations();
    }

    public List<Refugee> getRefugeesInShelter(Long shelterId) {

        return refugeeRepository.getRefugeesInShelter(shelterId);
    }

    public void removeRefugeeFromShelter(Long refugeeId) {

        refugeeRepository.removeRefugeeFromShelter(refugeeId);
    }

    public List<Refugee> getUsersWithoutShelter() {

        return refugeeRepository.getUsersWithoutShelter();
    }

    public Refugee getRefugeeByUserId(Long userId) {

        return refugeeRepository.getRefugeeByUserID(userId);
    }

    public void addRefugeeToShelter(Long shelterId, Long refugeeId) {

        refugeeRepository.addRefugeeToShelter(shelterId, refugeeId);
    }

    public Refugee getRefugeeByPhone(String phoneNumber) {

        return refugeeRepository.getRefugeeByPhone(phoneNumber);
    }
}
