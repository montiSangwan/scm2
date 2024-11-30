package com.scm.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

@Component
public class UsernameHelper {
    
    public static String getEmailOfLoggedInUser(Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken) {

            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            String authorizedClientRegistrationId = token.getAuthorizedClientRegistrationId();

            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
                return defaultOAuth2User.getAttribute("email").toString();
            } else {
                return defaultOAuth2User.getAttribute("email") != null ? defaultOAuth2User.getAttribute("email").toString()
                        : defaultOAuth2User.getAttribute("login").toString() + "@gmail.com";
            }
            
        } else {
            return authentication.getName();
        }
    }
}
