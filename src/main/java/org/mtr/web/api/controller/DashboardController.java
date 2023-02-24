package org.mtr.web.api.controller;

import org.mtr.web.api.component.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {

    @Autowired
    UserSession userSession;

    @GetMapping(path="/dashboard")
    public ModelAndView viewDashboard(){
        ModelAndView mv;

        if(this.userSession != null && this.userSession.getEmail() != null && !this.userSession.getEmail().isEmpty()){
            mv = new ModelAndView("dashboard");
            mv.addObject("email", this.userSession.getEmail());
        } else {
            return new ModelAndView("redirect:/login");
        }

        return mv;
    }
}
