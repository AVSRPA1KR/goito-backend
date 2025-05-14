package com.goito.springboot.service;

import com.goito.springboot.entity.User;
import com.goito.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUserData) {
        return userRepository.findById(id).map(user -> {
            user.setUserId(id);
            user.setName(updatedUserData.getName());
            user.setEmail(updatedUserData.getEmail());
            user.setGender(updatedUserData.getGender());
            user.setDateOfBirth(updatedUserData.getDateOfBirth());
            user.setPhoneNumber(updatedUserData.getPhoneNumber());
            user.setPreferredSize(updatedUserData.getPreferredSize());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }


    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

