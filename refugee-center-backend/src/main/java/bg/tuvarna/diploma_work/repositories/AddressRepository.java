package bg.tuvarna.diploma_work.repositories;

import bg.tuvarna.diploma_work.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}