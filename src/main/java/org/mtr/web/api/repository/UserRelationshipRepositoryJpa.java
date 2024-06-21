package org.mtr.web.api.repository;

import org.mtr.web.api.repository.dao.UserRelationshipDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRelationshipRepositoryJpa extends JpaRepository<UserRelationshipDAO, Long> {

    List<UserRelationshipDAO> findByUserId(String userId);
    UserRelationshipDAO findByUserIdAndFriendId(String userId, String friendId);

}
