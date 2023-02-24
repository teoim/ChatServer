package org.mtr.web.api.controller;

import org.mtr.web.api.component.UserSession;
import org.mtr.web.api.controller.dto.LoginDTO;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthenticationController {

    @Autowired
    AuthenticationService authService;
    @Autowired
    UserSession userSession;

    @GetMapping(path="/login")
    public ModelAndView login(){
        return new ModelAndView("login");
    }

    @PostMapping(path="/login")
    public ModelAndView login(LoginDTO logindto){
        // TODO: validari

        ModelAndView mv = null;

        UserDAO user = authService.login(logindto);
        if(user != null){
            // Authentication succeeded
            userSession.setEmail(user.getEmail() + " - " + user.getNick());      // TODO: inlocuirea email-ului in sesiune cu un token jwt
            mv = new ModelAndView("redirect:/dashboard");
        } else  {
            // Authentication failed
            mv = new ModelAndView("redirect:/register");
        }
        return mv;
    }
}
