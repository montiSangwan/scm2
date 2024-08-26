package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.dao.UserDao;
import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helper.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


// Component -> to make autowired directly
@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserDao userDao;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            // add new sign in with google user into db
            User user = new User();
            user.setName(defaultOAuth2User.getAttribute("name").toString());
            user.setEmail(defaultOAuth2User.getAttribute("email").toString());
            user.setProfilePic(defaultOAuth2User.getAttribute("picture").toString());
            user.setPassword("password");
            user.setUserId(UUID.randomUUID().toString());
            user.setProvider(Providers.GOOGLE);
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setProviderUserId(defaultOAuth2User.getName());
            user.setRoleList(List.of(AppConstants.ROLE_USER));
            user.setAbout("This is created by google account");

            User dbUser = userDao.findByEmail(user.getEmail()).orElse(null);
            if (Objects.isNull(dbUser)) {
                userDao.save(user);
            }
            
            new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
    
}
