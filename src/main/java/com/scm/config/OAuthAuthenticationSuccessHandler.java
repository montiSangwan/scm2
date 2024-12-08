package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.dao.UserDao;
import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helper.AppConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// Component -> to make autowired directly
@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserDao userDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            String authorizedClientRegistrationId = token.getAuthorizedClientRegistrationId();

            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            User user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setRoleList(List.of(AppConstants.ROLE_USER));
            user.setEmailVerified(true);
            user.setEnabled(true);
            user.setPassword("dummy");

            if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {

                user.setEmail(defaultOAuth2User.getAttribute("email").toString());
                user.setProfilePic(defaultOAuth2User.getAttribute("picture").toString());
                user.setName(defaultOAuth2User.getAttribute("name").toString());
                user.setProviderUserId(defaultOAuth2User.getName());
                user.setProvider(Providers.GOOGLE);
                user.setAbout("This account is created using google.");
    
            } else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {

                String email = defaultOAuth2User.getAttribute("email") != null ? defaultOAuth2User.getAttribute("email").toString()
                        : defaultOAuth2User.getAttribute("login").toString() + "@gmail.com";
                String picture = defaultOAuth2User.getAttribute("avatar_url").toString();
                String name = defaultOAuth2User.getAttribute("login").toString();
                String providerUserId = defaultOAuth2User.getName();
    
                user.setEmail(email);
                user.setProfilePic(picture);
                user.setName(name);
                user.setProviderUserId(providerUserId);
                user.setProvider(Providers.GITHUB);
                user.setAbout("This account is created using github");
            }
    

            User dbUser = userDao.findByEmail(user.getEmail()).orElse(null);
            if (Objects.isNull(dbUser)) {
                userDao.save(user);
            }
            
            new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
    
}
