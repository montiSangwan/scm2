package com.scm.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scm.entities.Contact;
import com.scm.entities.User;

@Repository
public interface ContactDao extends JpaRepository<Contact, String> {

    // find the contact by user
    // custom finder method
    Page<Contact> findByUser(User user, Pageable pageable);

    Page<Contact> findByUserAndName(User user, String name, Pageable pageable);

    Page<Contact> findByUserAndEmail(User user, String email, Pageable pageable);

    Page<Contact> findByUserAndPhoneNumber(User user, String phoneNumber, Pageable pageable);

    // custom query method
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    List<Contact> getByUserId(@Param("userId") String userId);

}
