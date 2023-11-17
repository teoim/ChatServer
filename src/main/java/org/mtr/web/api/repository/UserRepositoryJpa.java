package org.mtr.web.api.repository;

import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.repository.dao.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface UserRepositoryJpa extends JpaRepository<UserDAO, Integer> {
    Optional<UserDAO> findByEmail(String email);

    ArrayList<UserDAO> getUsersByEmailLikeIgnoreCaseOrNickLikeIgnoreCase(String emailLike, String nickLike);

    UserDAO getUserByEmail(String myEmail);
}
