package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.models.Customer;
import bg.tuvarna.diploma_work.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer getCustomerByUserID(Long id) {
        return customerRepository.getCustomerByUserID(id);
    }
}
