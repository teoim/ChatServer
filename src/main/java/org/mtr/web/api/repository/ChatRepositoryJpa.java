package org.mtr.web.api.repository;

import org.mtr.web.api.controller.dto.TextMessageDTO;
import org.mtr.web.api.repository.dao.TextMessageDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository
public interface ChatRepositoryJpa extends JpaRepository<TextMessageDAO, Timestamp> {

    TextMessageDAO save(TextMessageDAO message);

    List<TextMessageDAO> getMessagesByTxtFrom(String username);

    List<TextMessageDAO> getMessagesByTxtTo(String username);

    List<TextMessageDAO> getMessagesByTxtToAndTimestampGreaterThan(String username, Timestamp afterTimestamp);

    List<TextMessageDAO> getMessagesByTxtFromInAndTxtToIn(Collection<String> fromUserEmails, Collection<String>  toUsersEmails);

    List<TextMessageDAO> getMessagesByTxtFromInAndTxtToInAndTimestampGreaterThan(Collection<String> fromUserEmails, Collection<String> toUsersEmails, Timestamp afterTimestamp);
}
