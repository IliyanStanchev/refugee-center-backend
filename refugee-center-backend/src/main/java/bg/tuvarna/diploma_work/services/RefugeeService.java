package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Refugee;
import bg.tuvarna.diploma_work.repositories.RefugeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefugeeService {

    @Autowired
    private RefugeeRepository refugeeRepository;

    public Refugee getRefugeeByUserID(Long id) {
        return refugeeRepository.getRefugeeByUserID(id);
    }

    public Refugee createRefugee(Refugee refugee) {

        refugee.setId(0L);

        return refugeeRepository.save(refugee);
    }
}
