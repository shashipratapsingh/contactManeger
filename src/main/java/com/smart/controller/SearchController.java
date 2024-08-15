package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entity.Contact;
import com.smart.entity.User;

@RestController
public class SearchController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    // Search handler
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {
        User user = this.userRepository.findByEmail(principal.getName());
        
        // Find contacts by name containing the query string for the current user
        
        List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }
}
