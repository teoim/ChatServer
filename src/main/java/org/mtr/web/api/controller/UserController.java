package org.mtr.web.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.mtr.logger.ErrorLogger;
import org.mtr.logger.MessageLogger;
import org.mtr.web.api.controller.dto.TextMessageDTO;
import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.repository.dao.UserRelationshipDAO;
import org.mtr.web.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path="/users/{email}")
    public ModelAndView getUserByEmail(@PathVariable(name="email") String userEmail){
        // TEST METHOD
        MessageLogger.log( "UserController - getUserByEmail() - get");

        ModelAndView mv = new ModelAndView("userByEmail");
        mv.addObject("user", this.userService.getUserByEmail(userEmail));

        return mv;
    }


    @RequestMapping(
            value = "/searchUserByEmailOrNick/{textInput}",       // TODO: Secure endpoint (any logged user can see messages by any other user)
            method = RequestMethod.GET,
            produces = "application/json"
    )
    @ResponseBody
    public List<UserDTO> searchUserByEmailOrNick(@PathVariable(name="textInput") String textInput){
        MessageLogger.log("UserController - searchUserByEmailOrNick(...) - @RequestMapping(\"searchUserByEmailOrNick/{textInput}\")");
        ArrayList<UserDTO> searchMatches = null;

        textInput = "%" + textInput + "%";

        searchMatches = (ArrayList<UserDTO>) userService.searchUserByEmailLikeOrNickLike(textInput);

        return searchMatches;
    }


    @RequestMapping(
            value = "/addUserToFriendsList",       // TODO: Secure endpoint ?
            method = RequestMethod.PUT,
            produces = "application/json"
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

        UserRelationshipDAO newRelationship = this.userService.addUserToFriendsList( principal.getName(), newFriendEmail);

        return "New relationship:  " + newRelationship.getFriendId() + " is " + newRelationship.getStatus();
    }

    @RequestMapping(
            value = "/blockUser",       // TODO: Secure endpoint ?
            method = RequestMethod.PUT,
            produces = "application/json"
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

        UserRelationshipDAO newRelationship = this.userService.blockUser( principal.getName(), blockedUserEmail);

        return "Blocked relationship:  " + newRelationship.getFriendId() + " is " + newRelationship.getStatus();
    }
}
