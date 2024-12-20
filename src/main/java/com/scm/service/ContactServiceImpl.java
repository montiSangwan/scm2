package com.scm.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.scm.dao.ContactDao;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helper.ResourceNotFoundException;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactDao contactDao;

    public ContactServiceImpl(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    @Override
    public Contact save(Contact contact) {
        String contractId = UUID.randomUUID().toString();
        contact.setId(contractId);
        return contactDao.save(contact);
    }

    @Override
    public Contact update(Contact contact) {
        var dbContact = contactDao.findById(contact.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        dbContact.setName(contact.getName());
        dbContact.setEmail(contact.getEmail());
        dbContact.setPhoneNumber(contact.getPhoneNumber());
        dbContact.setAddress(contact.getAddress());
        dbContact.setDescription(contact.getDescription());
        dbContact.setPicture(contact.getPicture());
        dbContact.setFavourite(contact.isFavourite());
        dbContact.setWebsiteLink(contact.getWebsiteLink());
        dbContact.setLinkedInLink(contact.getLinkedInLink());
        dbContact.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

        return contactDao.save(dbContact);
    }

    @Override
    public List<Contact> getAll() {
        return contactDao.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
    }

    @Override
    public void delete(String id) {
        var contact = contactDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with given id " + id));
        contactDao.delete(contact);
    }

    @Override
    public Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user) {

        Sort sort = Objects.equals(order, "desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return contactDao.findByUserAndName(user, nameKeyword, pageRequest);
    }

    @Override
    public Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order,
            User user) {

        Sort sort = Objects.equals(order, "desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        
        return contactDao.findByUserAndEmail(user, emailKeyword, pageRequest);
    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy, String order,
            User user) {
        
        Sort sort = Objects.equals(order, "desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
                
        return contactDao.findByUserAndPhoneNumber(user, phoneNumberKeyword, pageRequest);
    }

    @Override
    public List<Contact> getByUserId(String userId) {
        return contactDao.getByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String direction) {

        // create sort object used for sorting from sortBy and direction
        Sort sort = Objects.equals(direction, "desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        PageRequest pageable = PageRequest.of(page, size, sort);

        return contactDao.findByUser(user, pageable);
    }

}
