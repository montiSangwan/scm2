package com.scm.helper;

import org.springframework.stereotype.Component;

@Component
public class EmailHelper {
    
    public static String getLinkForEmailVerification(String emailToken) {

        String link = "http://localhost:8081/auth/verify-email?token=" + emailToken;
        return link;
    }
}
