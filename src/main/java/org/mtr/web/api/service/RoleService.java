package org.mtr.web.api.service;

import org.mtr.web.api.repository.RoleRepositoryJPA;
import org.mtr.web.api.repository.dao.RoleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    RoleRepositoryJPA roleRepositoryJPA;

    public RoleDAO createRole(RoleDAO newRole) {
        return roleRepositoryJPA.save( newRole);
    }
}
