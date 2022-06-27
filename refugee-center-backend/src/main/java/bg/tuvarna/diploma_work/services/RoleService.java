package bg.tuvarna.diploma_work.services;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import bg.tuvarna.diploma_work.models.Role;
import bg.tuvarna.diploma_work.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    //Members
    //-----------------
    @Autowired
    private RoleRepository roleRepository;

    //Methods
    //-----------------
    public Role getRole(RoleType roleType) {
        return roleRepository.getRole(roleType);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }
}
