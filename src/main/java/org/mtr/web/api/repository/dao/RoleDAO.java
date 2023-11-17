package org.mtr.web.api.repository.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class RoleDAO implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;


//    @ManyToMany(mappedBy = "userRoles")     // mapped by UserDAO.roles list
//    private List<UserDAO> userRoles = new ArrayList<>();

    @Override
    public String getAuthority() {
        return this.getName();
    }
}
