package org.mtr.web.api.repository;

import org.mtr.web.api.repository.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<UserDAO, Integer> {
    public UserDAO findByEmailAndPassword(String email, String password);
}
