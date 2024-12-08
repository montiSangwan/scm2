package com.scm.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scm.dao.UserDao;
import com.scm.entities.User;
import com.scm.helper.AppConstants;
import com.scm.helper.EmailHelper;
import com.scm.helper.ResourceNotFoundException;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    private final EmailService emailService;

    // to store the encoded password in db
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, EmailServiceImpl emailService, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User saveUser(User user) {

        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);

        // password encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // set the user role
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        // to send verification link after saving new user to his email
        String emailToken = UUID.randomUUID().toString();
        String emailLink = EmailHelper.getLinkForEmailVerification(emailToken);
        emailService.sendEmail(user.getEmail(), "Verify Account: SCM", emailLink);

        user.setEmailToken(emailToken);
        User savedUser = userDao.save(user);
        return savedUser;
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User dbUser = userDao.findById(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User is not present"));

        dbUser.setName(user.getName());
        dbUser.setEmail(user.getEmail());
        dbUser.setPassword(user.getPassword());
        dbUser.setAbout(user.getAbout());
        dbUser.setPhoneNumber(user.getPhoneNumber());
        dbUser.setProfilePic(user.getProfilePic());
        dbUser.setEnabled(user.isEnabled());
        dbUser.setEmailVerified(user.isEmailVerified());
        dbUser.setPhoneVerified(user.isPhoneVerified());
        dbUser.setProvider(user.getProvider());
        dbUser.setProviderUserId(user.getProviderUserId());

        User savedUser = userDao.save(dbUser);
        return Optional.of(savedUser);
    }

    @Override
    public void deleteUser(String id) {
        User dbUser = userDao.findById(id).orElseThrow(() -> new ResourceNotFoundException("User is not present"));
        userDao.delete(dbUser);
    }

    @Override
    public boolean isUserExist(String userId) {
        User user = userDao.findById(userId).orElse(null);
        return user != null;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = userDao.findByEmail(email).orElse(null);
        return user != null;
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email).orElse(null);
    }
}
