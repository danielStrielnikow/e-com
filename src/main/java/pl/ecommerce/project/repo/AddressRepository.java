package pl.ecommerce.project.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ecommerce.project.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
