package org.mtr.web.api.service;

import org.mtr.logger.MessageLogger;
import org.mtr.web.api.controller.dto.UserRelationshipDTO;
import org.mtr.web.api.repository.UserRelationshipRepositoryJpa;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class UserRelationshipService {

    @Autowired
    UserRelationshipRepositoryJpa userRelationshipJpa;

    public UserRelationshipDTO getRelationshipBetween(String thisEmail, String thatEmail){
        return userRelationshipJpa.findByUserIdAndFriendId(thisEmail, thatEmail).toDto();
    }

    public UserRelationshipDAO addUserToFriendsList(String myEmail, String myNewFriendEmail) {
        MessageLogger.log( "UserService - addUserToFriendsList(String,String)");

        UserRelationshipDAO relationship = this.userRelationshipJpa.findByUserIdAndFriendId(myEmail, myNewFriendEmail);

        if(relationship==null){
            relationship = new UserRelationshipDAO();
            relationship.setUserId(myEmail);
            relationship.setFriendId(myNewFriendEmail);
            relationship.setStatus("FRIEND");
            relationship.setInRelationshipSince( new Timestamp( System.currentTimeMillis()));
            relationship = this.userRelationshipJpa.save(relationship);
        } else {
            MessageLogger.log("User " + myNewFriendEmail + " is already a friend of " + myEmail);
        }

        return relationship;
    }

    public UserRelationshipDAO blockUser(String myEmail, String blockedUserEmail) {
        MessageLogger.log( "UserService - blockUser(String,String)");

        UserRelationshipDAO relationship = this.userRelationshipJpa.findByUserIdAndFriendId(myEmail, blockedUserEmail);
        relationship.setStatus("FOE");

        this.userRelationshipJpa.save(relationship);

        return relationship;
    }
}
