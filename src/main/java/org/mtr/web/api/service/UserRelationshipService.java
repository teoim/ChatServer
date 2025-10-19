package org.mtr.web.api.service;

import org.mtr.logger.MessageLogger;
import org.mtr.web.api.controller.dto.UserRelationshipDTO;
import org.mtr.web.api.repository.UserRelationshipRepositoryJpa;
import org.mtr.web.api.repository.UserRepositoryJpa;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;
import org.mtr.web.api.utilities.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserRelationshipService {

    @Autowired
    UserRelationshipRepositoryJpa userRelationshipJpa;

    @Autowired
    UserRepositoryJpa userRepositoryJpa;

    public UserRelationshipDTO getRelationshipBetween(String thisEmail, String thatEmail){
        MessageLogger.log( "UserRelationshipService - getRelationshipBetween(String,String)");

        Optional<UserRelationshipDAO> dbResult = userRelationshipJpa.findByUserIdAndFriendId(thisEmail, thatEmail);

        return dbResult.map(UserRelationshipDAO::toDto).orElse(null);

    }

    public UserRelationshipDAO addUserToFriendsList(String myEmail, String myNewFriendEmail) {
        MessageLogger.log( "UserRelationshipService - addUserToFriendsList(String)");

        final List<String> validationResult = validateEmail(List.of(myEmail, myNewFriendEmail));
        if(!validationResult.isEmpty()){
            throw new RuntimeException(validationResult.toString());
        };

        UserRelationshipDAO relationship = findOrCreateRelationship(myEmail, myNewFriendEmail);
        if(!relationship.getStatus().isEmpty()) {
            MessageLogger.log("User " + myNewFriendEmail + " relationship with " + myEmail + " - status: " + relationship.getStatus());
        }
        relationship.setStatus("FRIEND");
        relationship = this.userRelationshipJpa.save(relationship);

        return relationship;
    }

    public UserRelationshipDAO blockUser(String myEmail, String blockedUserEmail) {
        MessageLogger.log( "UserRelationshipService - blockUser(String,String)");

        UserRelationshipDAO relationship = findOrCreateRelationship(myEmail, blockedUserEmail);
        relationship.setStatus("FOE");
        relationship.setLastUpdated(Utilities.getCurrentTimestamp());

        this.userRelationshipJpa.save(relationship);

        return relationship;
    }

    /**
     * ==================== Utility methods ====================
     * */

    private List<String> validateEmail(List<String> emailList) {
        List<String> errorMessages = new ArrayList<>();
        for(String email : emailList){
            if(!userRepositoryJpa.existsByEmail(email)){
                errorMessages.add("User email is inexistent: " + email);
            };
        }
        return errorMessages;
    }

    private UserRelationshipDAO findOrCreateRelationship(String myEmail, String otherEmail) {
        return userRelationshipJpa.findByUserIdAndFriendId(myEmail, otherEmail)
                .orElseGet(() -> UserRelationshipDAO.builder()
                        .userId(myEmail)
                        .friendId(otherEmail)
                        .status("")     // empty status will identify a new relationship
                        .inRelationshipSince(Utilities.getCurrentTimestamp())
                        .build());
    }

}
