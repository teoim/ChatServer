package org.mtr.web.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;
import org.mtr.web.api.controller.dto.UserRelationshipDTO;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;
import org.mtr.web.api.service.UserRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.Principal;
import java.util.stream.Collectors;

@RestController
public class UserRelationshipController {

    @Autowired
    UserRelationshipService userRelationshipService;

    @GetMapping("/relationships/{thatEmail}")
    public HttpEntity<UserRelationshipDTO> getRelationshipBetweenUsers(@PathVariable String thatEmail, Principal principal){
        MessageLogger.log("UserRelationshipController - getRelationshipBetweenUsers(Principal, String)");
        return ResponseEntity.ok()
                .body(userRelationshipService.getRelationshipBetween(principal.getName(), thatEmail));
    }

    @RequestMapping(
            value = "/addUserToFriendsList",       // TODO: Secure endpoint ?
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
//    @CrossOrigin(origins = "http://localhost:8080", methods = {RequestMethod.PUT})
    public HttpEntity<String> addUserToFriendsList(HttpServletRequest request, Principal principal){
        MessageLogger.log("UserRelationshipController - addUserToFriendsList(...) - @RequestMapping(\"addUserToFriendsList\")");

        String newFriendEmail = null;
        try {
            newFriendEmail = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if(newFriendEmail.isBlank()){
                return ResponseEntity.badRequest().body("UserController - addUserToFriendsList - newFriendEmail is null");
            }
        } catch (IOException e) {
            ErrorLogger.log(e, this.getClass().getSimpleName(), "addUserToFriendsList(HttpServletRequest)");
        }

        UserRelationshipDAO newRelationship = null;
        try {
            newRelationship = this.userRelationshipService.addUserToFriendsList(principal.getName(), newFriendEmail);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("New relationship:  " + newRelationship.getFriendId() + " is " + newRelationship.getStatus());
    }

    @RequestMapping(
            value = "/blockUser",       // TODO: Secure endpoint ?
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
//    @CrossOrigin(origins = "http://localhost:8080", methods = {RequestMethod.PUT})
    public ResponseEntity<String> blockUser(HttpServletRequest request, Principal principal){
        MessageLogger.log("UserRelationshipController - blockUser(...) - @RequestMapping(\"blockUser\")");

        String blockedUserEmail = null;
        try {
            blockedUserEmail = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            ErrorLogger.log(e, this.getClass().getSimpleName(), "addUserToFriendsList(HttpServletRequest)");
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        UserRelationshipDAO newRelationship = this.userRelationshipService.blockUser( principal.getName(), blockedUserEmail);

        return ResponseEntity.ok(
                "Blocked relationship:  " + newRelationship.getFriendId() + " is " + newRelationship.getStatus());
    }
}
