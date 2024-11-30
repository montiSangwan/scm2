package com.scm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.scm.service.SecurityCustomUserDetailService;

@Configuration
public class SecurityConfig {

    private final SecurityCustomUserDetailService userDetailService;

    private final OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler;

    private final AuthFailureHandler authFailureHandler;

    public SecurityConfig(SecurityCustomUserDetailService userDetailService,
            OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler,
            AuthFailureHandler authFailureHandler) {
        this.userDetailService = userDetailService;
        this.oAuthAuthenticationSuccessHandler = oAuthAuthenticationSuccessHandler;
        this.authFailureHandler = authFailureHandler;
    }

    // configuraiton of authentication provider for spring security
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // user detail service ka object:
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        // password encoder ka object
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // filter out the url that will be secured and other will be public
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // url configuration which url are public or private
        httpSecurity.authorizeHttpRequests(authorize -> {
            // authorize.requestMatchers("/home", "/register").permitAll(); make these url public
            authorize.requestMatchers("/user/**").authenticated(); // authenticated url's started with user authenticated
            authorize.anyRequest().permitAll();
        });

        // login using username and password
        // if access denied then login form will come instead of exception
        httpSecurity.formLogin(formLogin -> {
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/authenticate"); //login form submit at this url
            formLogin.defaultSuccessUrl("/user/profile"); // error resolved
            /* formLogin.failureForwardUrl("/login?error=true"); this requires postMapping of login*/
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");

            // if authentication is failed
            formLogin.failureHandler(authFailureHandler); 
        });

        // for logout 
        // disable csrf show that logout url hit by any http request
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.logout(logout -> {
            logout.logoutUrl("/logout");  // url used in user_navbar
            logout.logoutSuccessUrl("/login?logout=true"); // after successful logout redirect to login
        });


        // OAuth configuration
        httpSecurity.oauth2Login(oauth -> {
            oauth.loginPage("/login");
            oauth.successHandler(oAuthAuthenticationSuccessHandler);
        });

        return httpSecurity.build();
    }
}