package pl.ecommerce.project.repo;

import pl.ecommerce.project.model.Role;
import pl.ecommerce.project.model.app.AppRole;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByRoleName(AppRole appRole);
}
