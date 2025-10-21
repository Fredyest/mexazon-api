package com.mexazon.app.controller;

import com.mexazon.app.model.User;
import com.mexazon.app.service.impl.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---------- CREATE ----------
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User newUser) {
        User created = userService.createUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ---------- READ ----------
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        User found = userService.getById(userId);
        return ResponseEntity.ok(found);
    }
}

