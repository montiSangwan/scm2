package com.scm.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.dao.UserDao;
import com.scm.entities.User;
import com.scm.helper.Message;
import com.scm.helper.MessageType;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserDao userDao;

    public AuthController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, HttpSession httpSession) {

        Optional<User> optionalUser = userDao.findByEmailToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getEmailToken().equals(token)) {
                Message message = Message.builder()
                        .content("You email is verified. Now you can login.")
                        .type(MessageType.green)
                        .build();
                httpSession.setAttribute("message", message);

                user.setEmailVerified(true);
                user.setEnabled(true);
                userDao.save(user);
                
                return "success_page";

            } else {
                Message message = Message.builder()
                        .content("Email not verified ! Token is not associated with user.")
                        .type(MessageType.red)
                        .build();
                httpSession.setAttribute("message", message);

                return "error_page";
            }
        }

        Message message = Message.builder()
                .content("Email not verified ! Token is not associated with user.")
                .type(MessageType.red)
                .build();
        httpSession.setAttribute("message", message);

        return "error_page";

    }

}
