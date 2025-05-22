package com.goito.springboot.controller;

import com.goito.springboot.entity.User;
import com.goito.springboot.entity.UserAddress;
import com.goito.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/hello")
    public String helloUser() { return "Welcome to GoIto"; }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByEmail(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user, @PathVariable UUID id) {
        return userService.updateUser(id, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable UUID id) {
        return userService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User with ID " + id + " successfully deleted.");
    }

    // User Address endpoints

    @GetMapping("/{userId}/addresses")
    public List<UserAddress> getUserAddresses(@PathVariable UUID userId) {
        return userService.getUserAddresses(userId);
    }

    @PostMapping("/{userId}/addresses")
    public UserAddress addUserAddress(@PathVariable UUID userId, @RequestBody UserAddress address) {
        return userService.addUserAddress(userId, address);
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    public UserAddress updateUserAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId,
            @RequestBody UserAddress address) {
        return userService.updateUserAddress(userId, addressId, address);
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<String> deleteUserAddress(
            @PathVariable UUID userId,
            @PathVariable UUID addressId) {
        userService.deleteUserAddress(userId, addressId);
        return ResponseEntity.ok("Address successfully deleted");
    }
}