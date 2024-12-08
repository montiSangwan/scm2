package com.scm.config;

import java.io.IOException;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.scm.helper.Message;
import com.scm.helper.MessageType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        if (exception instanceof DisabledException) {
            
            Message message = Message.builder()
                .content("User is disabled, Email with verification link is send to your email id !!")
                .type(MessageType.red)
                .build();
            HttpSession httpSession = request.getSession();    
            httpSession.setAttribute("message", message);

            response.sendRedirect("/login");

        } else {
            response.sendRedirect("/login?error=true");
        }
    }
    
}
