package bg.tuvarna.diploma_work.models;

import bg.tuvarna.diploma_work.enumerables.RoleType;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ROLES")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition ="serial")
    private Long id;

    @NotNull
    private RoleType roleType;

    public Role() {
    }

    public Role(RoleType roleType) {
        this.roleType = roleType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && roleType == role.roleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleType);
    }
}
