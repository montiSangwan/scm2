package com.scm.entities;

import java.util.*;
import java.util.stream.Collectors;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/*Spring Security only accept UserDeatils object so to reuse User we implements UserDeatils in User */
/*Email used as username while login */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    private String userId;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private String phoneNumber;
    @Column(length = 1000)
    private String about;
    private String profilePic;

    // for email verification
    private boolean enabled = false; // by default user is disabled
    private boolean emailVerified = false;
    private boolean phoneVerified = false;
    private String emailToken;

    // to store the default value
    @Enumerated(value = EnumType.STRING)
    private Providers provider = Providers.SELF;
    private String providerUserId;

    /*
    * The LazyInitializationException in Hibernate typically occurs when you attempt to access a lazily-loaded association outside of an active Hibernate session.

    * In your case, it seems like you might be trying to access the contacts list in your User entity after the session has closed, likely in a situation
        where the user details are being logged or printed.

    * Eager Fetching -> not a good method to use instead use DTO.

    * Changed to FetchType - LAZY, in EAGER contact are not deleted
    */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contact> contacts = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roleList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // list of roles[USER,ADMIN]
        // Collection of SimpGrantedAuthority[roles{ADMIN,USER}]
        Collection<SimpleGrantedAuthority> roles = roleList.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
        return roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /*
    * The StackOverflowError indicates that you likely have a circular reference in your entity relationships, which is causing infinite recursion when trying
        to serialize your objects (for example, when calling toString() or during JSON serialization).

    * When you fetch User eagerly, it retrieves the associated contacts, and each Contact has a reference back to the User.

    * Override toString()
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + (password != null ? "**" : null) + '\'' +  // Masking the password for security
                ", phoneNumber='" + phoneNumber + '\'' +
                ", about='" + about + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", enabled=" + enabled +
                ", emailVerified=" + emailVerified +
                ", phoneVerified=" + phoneVerified +
                ", provider=" + provider +
                ", providerUserId='" + providerUserId + '\'' +
                '}';
    }

}