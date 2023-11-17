package org.mtr.web.api.repository;

import org.mtr.web.api.repository.dao.RolesIdSeqDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesIdSeqRepositoryJPA extends JpaRepository<RolesIdSeqDAO, Integer> {

}
