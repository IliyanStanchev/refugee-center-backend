package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.models.Customer;
import bg.tuvarna.diploma_work.models.Log;
import bg.tuvarna.diploma_work.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.user.id = ?1")
    Customer getCustomerByUserID(Long userID);
}