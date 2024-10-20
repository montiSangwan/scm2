package com.scm.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.helper.Message;
import com.scm.helper.MessageType;
import com.scm.helper.UsernameHelper;
import com.scm.service.ContactService;
import com.scm.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private final ContactService contactService;

    private final UserService userService;

    public ContactController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }

    // to receive contactForm data we will send empty contactForm object in model
    @RequestMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        // to autofill any details to show on ui then add here in contactForm object
        model.addAttribute("contactForm", contactForm);

        return "user/add_contact";
    }

    @PostMapping("/process-contact")
    public String processContactForm(@Valid @ModelAttribute ContactForm contactForm, Authentication authentication,
            HttpSession httpSession, BindingResult bindingResult) {

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
        contact.setFavourite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setUser(user);

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
}
