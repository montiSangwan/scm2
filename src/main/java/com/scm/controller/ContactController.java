package com.scm.controller;

import static com.scm.helper.AppConstants.SEARCH_BY_EMAIL;
import static com.scm.helper.AppConstants.SEARCH_BY_NAME;
import static com.scm.helper.AppConstants.SEARCH_BY_PHONE_NUMBER;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helper.ImageValidationException;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.helper.UsernameHelper;
import com.scm.service.ContactService;
import com.scm.service.ImageService;
import com.scm.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user/contacts")
@Slf4j
public class ContactController {

    private final ContactService contactService;

    private final UserService userService;

    private final ImageService imageService;

    public ContactController(ContactService contactService, UserService userService, ImageService imageService) {
        this.contactService = contactService;
        this.userService = userService;
        this.imageService = imageService;
    }

    // to receive contactForm data we will send empty contactForm object in model
    @RequestMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        // to autofill any details to show on ui then add here in contactForm object
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    /* @Valid annotation is present before @ModelAttribute.
    *  BindingResult immediately follows @ModelAttribute. 
    */
    @PostMapping("/process-contact")
    public String processContactForm(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, Authentication authentication,
            HttpSession httpSession) throws IOException {

        // validate form data
        if (bindingResult.hasErrors()) {

            httpSession.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());

            return "user/add_contact";
        }

        String userName = UsernameHelper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(userName);

        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavourite(contactForm.isFavourite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setUser(user);

        MultipartFile contactImage = contactForm.getContactImage();
        // Validate the image before proceeding
        imageService.validateImage(contactImage);  // This is where the validation happens
        
        if (Objects.nonNull(contactImage) && contactImage.getInputStream().available() > 0) {
            // upload image on cloud/server
            try {
                String imageFileName = UUID.randomUUID().toString();
                String fileURL = imageService.uploadImage(contactImage, imageFileName);
                
                contact.setPicture(fileURL);
                contact.setCloudinaryImagePublicId(imageFileName);
            
            } catch (Exception e) {
                log.error("An error occurred while uploading the image: {}" + e.getMessage());
                throw new ImageValidationException("Image upload failed due to an unexpected error. %s", e.getMessage());
            }
        }

        Contact savedContact = contactService.save(contact);
        System.out.println(savedContact);

        // message = "Contact creation successful"
        Message message = Message.builder()
                .content("Contact created successfully")
                .type(MessageType.green)
                .build();
        httpSession.setAttribute("message", message);

        // redirect to add contact page
        return "redirect:/user/contacts/add";
    }

    /*
     * page -> which page do you want to see
     * size -> how many contacts you want to see on that page
     * sortBy -> on which basis are you wanting sorting
     * direction -> direction of sorting
     */
    // request param used by /contacts?page=1&size=10?sortBy=name
    @RequestMapping
    public String viewContacts(Model model, Authentication authentication, 
            @RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {

        // find email of logged-in user through authentication
        String emailOfLoggedInUser = UsernameHelper.getEmailOfLoggedInUser(authentication);

        // find user from user service using email
        User user = userService.getUserByEmail(emailOfLoggedInUser);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);
        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", size);
        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    // ModelAttribute if we used object's field in request param
    @RequestMapping("/search")
    public String searchHandler(Model model, Authentication authentication,
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "page", defaultValue = "0") int page, 
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
    
        String field = contactSearchForm.getField();
        String keyword = contactSearchForm.getKeyword();

        String emailOfLoggedInUser = UsernameHelper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(emailOfLoggedInUser);

        Page<Contact> pageContact = null;
        if (StringUtils.isNotBlank(field)) {
            if (field.equalsIgnoreCase(SEARCH_BY_NAME)) {
                pageContact = contactService.searchByName(keyword, size, page, sortBy, direction, user);
            } else if (field.equalsIgnoreCase(SEARCH_BY_EMAIL)) {
                pageContact = contactService.searchByEmail(keyword, size, page, sortBy, direction, user);
            } else if (field.equalsIgnoreCase(SEARCH_BY_PHONE_NUMBER)) {
                pageContact = contactService.searchByPhoneNumber(keyword, size, page, sortBy, direction, user);
            }
        } else {
            pageContact = contactService.searchByName(keyword, size, page, sortBy, direction, user);
        }

        model.addAttribute("pageSize", size);
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("contactSearchForm", contactSearchForm);
        return "user/search";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") String contactId, HttpSession httpSession) {
        contactService.delete(contactId);

        // message = "Contact deleted successful"
        Message message = Message.builder()
                .content("Contact deleted successfully")
                .type(MessageType.green)
                .build();
        httpSession.setAttribute("message", message);

        return "redirect:/user/contacts";
    }

    @RequestMapping("/view/{id}")
    public String viewContact(@PathVariable("id") String contactId, Model model) {
        Contact contact = contactService.getById(contactId);
        if (Objects.nonNull(contact)) {
            model.addAttribute("contact", contact);
            return "user/view_contact";
        } else {
            return "redirect:/user/contacts";
        }
    }

    @RequestMapping("/updateContactView/{id}")
    public String updateContactView(@PathVariable("id") String contactId, Model model) {
        Contact contact = contactService.getById(contactId);
        if (Objects.nonNull(contact)) {

            ContactForm contactForm = new ContactForm();
            contactForm.setName(contact.getName());
            contactForm.setEmail(contact.getEmail());
            contactForm.setPhoneNumber(contact.getPhoneNumber());
            contactForm.setAddress(contact.getAddress());
            contactForm.setDescription(contact.getDescription());
            contactForm.setFavourite(contact.isFavourite());
            contactForm.setWebsiteLink(contact.getWebsiteLink());
            contactForm.setLinkedInLink(contact.getLinkedInLink());
            contactForm.setPicture(contact.getPicture());

            model.addAttribute("contactForm", contactForm);
            model.addAttribute("contactId", contact.getId());
            return "user/update_contact_view";
        } else {
            return "redirect:/user/contacts";
        }
    }

    /* @Valid annotation is present before @ModelAttribute.
    *  BindingResult immediately follows @ModelAttribute. 
    */
    @PostMapping("/process-update-contact/{contactId}")
    public String processUpdateContactForm(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, @PathVariable("contactId") String contactId,
            HttpSession httpSession) throws IOException {

        // validate form data
        if (bindingResult.hasErrors()) {

            httpSession.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());

            return "user/update_contact_view";
        }

        Contact contact = contactService.getById(contactId);
        contact.setId(contactId);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavourite(contactForm.isFavourite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());

        MultipartFile contactImage = contactForm.getContactImage();
        // Validate the image before proceeding
        imageService.validateImage(contactImage);  // This is where the validation happens

        if (Objects.nonNull(contactImage) && contactImage.getInputStream().available() > 0) {
            // upload image on cloud/server
            try {
                String imageFileName = UUID.randomUUID().toString();
                String fileURL = imageService.uploadImage(contactImage, imageFileName);
                
                contact.setPicture(fileURL);
                contact.setCloudinaryImagePublicId(imageFileName);
            
            } catch (Exception e) {
                log.error("An error occurred while uploading the image: {}" + e.getMessage());
                throw new ImageValidationException("Image upload failed due to an unexpected error. %s", e.getMessage());
            }
        }

        Contact updatedContact = contactService.update(contact);
        System.out.println(updatedContact);

        // message = "Contact creation successful"
        Message message = Message.builder()
                .content("Contact updated successfully")
                .type(MessageType.green)
                .build();
        httpSession.setAttribute("message", message);

        // redirect to add contact page
        return "redirect:/user/contacts/view/" + contactId;
    }
}
