package org.mtr.web.api.repository;

import org.mtr.web.api.repository.dao.RoleDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepositoryJPA extends JpaRepository<RoleDAO, Integer> {

    List<RoleDAO> findByName(String name);

    RoleDAO save(RoleDAO roleDAO);
}
