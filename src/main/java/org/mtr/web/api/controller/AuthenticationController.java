package org.mtr.web.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.mtr.logger.MessageLogger;
import org.mtr.web.api.component.UserSession;
import org.mtr.web.api.controller.dto.AuthenticationDTO;
import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.repository.dao.UserDAO;
import org.mtr.web.api.service.AuthenticationService;
import org.mtr.web.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AuthenticationService authService;
    @Autowired
    UserService userService;
    @Autowired
    UserSession userSession;    // setat in authService.login

    @Autowired
    private Environment env;

    @GetMapping(path="/authenticate")
    public ModelAndView login(){
        MessageLogger.log( "AuthenticationController - login() - get");

        if(isAuthenticated()){
            return new ModelAndView("redirect:/dashboard");
        }

        return new ModelAndView("authenticate");
    }

    @PostMapping(path="/authenticate-post")
    public ModelAndView login(AuthenticationDTO logindto, HttpServletRequest request){
        MessageLogger.log( "AuthenticationController - login(AuthenticationDTO) - post");

        // TODO: validari pe input

        if(isAuthenticated()){
            return new ModelAndView("redirect:/dashboard");
        }

        ModelAndView mv = null;

        UserDAO user = authService.login(logindto, request);
        if(user != null){
            // Authentication succeeded
            mv = new ModelAndView("redirect:/dashboard");
        } else  {
            // Authentication failed
//            mv = new ModelAndView("redirect:/api/auth/register");
            mv = new ModelAndView("redirect:/authenticate");
            mv.addObject("error", "Wrong username/password combination.");
        }
        return mv;
    }

    @GetMapping(path="/register")
    public ModelAndView register(){
        MessageLogger.log( "AuthenticationController - register() - get");
        return new ModelAndView("register");
    }

    @PostMapping(path="/register")
    public ModelAndView register(UserDTO newUser){
        MessageLogger.log( "AuthenticationController - register(UserDTO) - post");

        // TODO: Validari pe input

        //int x = this.userService.registerUser(newUser);
        //userSession.setEmail( "SQL insert code: " + x + " - " + newUser.getEmail());
        //MessageLogger.log( "User registration\nSQL insert code: " + x + " - " + newUser.getEmail());
        UserDAO newUserDao = this.userService.registerUser(newUser);
        MessageLogger.log( "User registration\nNew user: \n\t" + newUserDao.toString() + "\n\t - " + newUser.getEmail());
        return new ModelAndView("redirect:/dashboard");
    }

    // https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html
    @GetMapping(path="/logout")
    public ModelAndView logout(){
        MessageLogger.log( "AuthenticationController - logout() - get");
        ModelAndView mv = new ModelAndView("redirect:/");
        return mv;
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }
}
