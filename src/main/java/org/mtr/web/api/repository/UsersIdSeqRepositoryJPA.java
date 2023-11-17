package org.mtr.web.api.repository;

import org.mtr.web.api.repository.dao.UsersIdSeqDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersIdSeqRepositoryJPA extends JpaRepository<UsersIdSeqDAO, String> {

}
