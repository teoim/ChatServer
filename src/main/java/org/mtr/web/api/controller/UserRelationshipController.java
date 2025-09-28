package org.mtr.web.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;
import org.mtr.web.api.controller.dto.UserRelationshipDTO;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;
import org.mtr.web.api.service.UserRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collectors;

@RestController
public class UserRelationshipController {

    @Autowired
    UserRelationshipService userRelationshipService;

    @GetMapping("/relationships/{thatEmail}")
    public UserRelationshipDTO getRelationshipBetweenUsers(@PathVariable String thatEmail, Principal principal){
        MessageLogger.log("UserRelationshipController - getRelationshipBetweenUsers(Principal, String)");
        return userRelationshipService.getRelationshipBetween(principal.getName(), thatEmail);
    }

    @RequestMapping(
            value = "/addUserToFriendsList",       // TODO: Secure endpoint ?
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
//    @CrossOrigin(origins = "http://localhost:8080", methods = {RequestMethod.PUT})
    public String addUserToFriendsList(HttpServletRequest request, Principal principal){
        MessageLogger.log("UserController - addUserToFriendsList(...) - @RequestMapping(\"addUserToFriendsList\")");

        String newFriendEmail = null;
        try {
            newFriendEmail = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if(newFriendEmail.isBlank() || newFriendEmail==null){
                throw new IOException("UserController - addUserToFriendsList - newFriendEmail is null");
            }
        } catch (IOException e) {
            //throw new RuntimeException(e);
            ErrorLogger.log(e, this.getClass().getSimpleName(), "addUserToFriendsList(HttpServletRequest)");
        }

        UserRelationshipDAO newRelationship = this.userRelationshipService.addUserToFriendsList( principal.getName(), newFriendEmail);

        return "New relationship:  " + newRelationship.getFriendId() + " is " + newRelationship.getStatus();
    }

    @RequestMapping(
            value = "/blockUser",       // TODO: Secure endpoint ?
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
//    @CrossOrigin(origins = "http://localhost:8080", methods = {RequestMethod.PUT})
    public String blockUser(HttpServletRequest request, Principal principal){
        MessageLogger.log("UserController - blockUser(...) - @RequestMapping(\"blockUser\")");

        String blockedUserEmail = null;
        try {
            blockedUserEmail = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            //throw new RuntimeException(e);
            ErrorLogger.log(e, this.getClass().getSimpleName(), "addUserToFriendsList(HttpServletRequest)");
        }

        UserRelationshipDAO newRelationship = this.userRelationshipService.blockUser( principal.getName(), blockedUserEmail);

        return "Blocked relationship:  " + newRelationship.getFriendId() + " is " + newRelationship.getStatus();
    }
}
