package org.mtr.web.api.controller;

import org.mtr.logger.MessageLogger;
import org.mtr.web.api.component.UserSession;
import org.mtr.web.api.repository.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;

@Controller
public class DashboardController {

    @Autowired
    UserSession userSession;


    @GetMapping(path="/dashboard")
    public ModelAndView viewDashboard(@AuthenticationPrincipal UserDAO customUser, CsrfToken token){
        MessageLogger.log( "DashboardController - viewDashboard() - get");

        ModelAndView mv;
        String email, roles = "";
        Object principal;
        Collection<? extends GrantedAuthority> authorities;

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        email = authentication.getName();
        for( GrantedAuthority role : authentication.getAuthorities() ){
            roles += "{" + role.getAuthority() + "}";
        }
        principal = authentication.getPrincipal();
        authorities = authentication.getAuthorities();


        //if(this.userSession != null && this.userSession.getEmail() != null && !this.userSession.getEmail().isEmpty()){
            mv = new ModelAndView("dashboard");
            //mv.addObject("email", this.userSession.getEmail() != null ? this.userSession.getEmail() : "email-not-defined" );
            mv.addObject("email", email != null ? email : "email-not-defined" );
            //mv.addObject("nick", this.userSession.getUsername() != null ? this.userSession.getUsername() : "username-not-defined" );
            mv.addObject("role", !roles.equals("") ? roles : "role-not-defined" );
        //} else {
        //    return new ModelAndView("redirect:/api/auth/authenticate");
        //}

        return mv;
    }
}
