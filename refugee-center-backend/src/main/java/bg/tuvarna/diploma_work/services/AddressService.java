package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Address;
import bg.tuvarna.diploma_work.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    public Address saveAddress(Address address) {

        address.setId(0L);
        return addressRepository.save(address);
    }
}
