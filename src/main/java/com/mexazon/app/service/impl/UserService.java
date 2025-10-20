package com.mexazon.app.service.impl;

import org.springframework.stereotype.Service;

import com.mexazon.app.model.Business;
import com.mexazon.app.model.User;
import com.mexazon.app.repository.BusinessRepository;
import com.mexazon.app.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    public UserService(UserRepository userRepository, BusinessRepository businessRepository) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
    }

    @Transactional
    public User createUser(User newUser) {
        User saved = userRepository.save(newUser);

        if (saved.getUserType() == "business") {
            Business b = Business.fromUser(saved);
            businessRepository.save(b);
        }
        return saved;
    }
}