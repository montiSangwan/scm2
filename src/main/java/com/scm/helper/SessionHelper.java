package com.scm.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

// file to remove registeration message after refreshing

@Component
public class SessionHelper {
    
    public static void removeMessage() {
        try {
            HttpSession httpSession = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            httpSession.removeAttribute("message");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
