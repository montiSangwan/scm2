package com.scm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    private final UserService userService;
    
    public PageController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/home")
    public String home(Model model) {
        model.addAttribute("name", "monti");
        return "home";
    }

    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    @RequestMapping("/services")
    public String services() {
        return "services";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {

        // to fill the form data in userForm object
        // model is used to use userForm object in html file
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return "register";
    }

    // @Valid -> to validate the object
    // ModelAttribute -> populate the userForm model attribute with data from a form submitted to the processRegister endpoint
    // BindingResult -> if there any validation failure then that error save in bindingResult object
    // HttpSession -> to show successful message on current session
    @PostMapping("/do-register")
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, HttpSession httpSession) {

        // validate form data
        if (bindingResult.hasErrors()) {
            httpSession.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
                    
            return "register";
        }

        // save to database
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        
        User savedUser = userService.saveUser(user);
        System.out.println(savedUser);

        // message = "Registration successful"
        Message message = Message.builder()
                .content("Registration Successful")
                .type(MessageType.green)
                .build();
        httpSession.setAttribute("message", message);

        // redirect to register page
        return "redirect:/register";
    }
}
