package org.mtr.web.api.repository;

import org.mtr.web.api.repository.dao.TextMessageDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface ChatRepositoryJpa extends JpaRepository<TextMessageDAO, Timestamp> {

    TextMessageDAO save(TextMessageDAO message);
}
