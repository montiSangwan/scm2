package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.helper.UsernameHelper;
import com.scm.service.UserService;

// RootController -> ye controller har ek request ke liye chle ga user ko add krne ke liye

// ControllerAdvice -> is controller ka method har ek request me add ho jaaye ga
@ControllerAdvice
public class RootController {

    @Autowired
    private UserService userService;

    // ModelAttribute -> har ek request se phle ye wala method add ho jaaye ga
    @ModelAttribute
    public void addLoggedInUserDetails(Model model, Authentication authentication) {
        if (authentication != null) {
            String userName = UsernameHelper.getEmailOfLoggedInUser(authentication);

            User user = userService.getUserByEmail(userName);
            model.addAttribute("loggedInUser", user);
        }
    }

}
