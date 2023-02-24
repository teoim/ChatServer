package org.mtr.web.api.controller;

import org.mtr.web.api.controller.dto.UserDTO;
import org.mtr.web.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(path="/users/{email}")
    public ModelAndView getUserByEmail(@PathVariable(name="email") String userEmail){
        ModelAndView mv = new ModelAndView("userByEmail");
        mv.addObject("user", this.userService.getUserByEmail(userEmail));

        return mv;
    }
}
