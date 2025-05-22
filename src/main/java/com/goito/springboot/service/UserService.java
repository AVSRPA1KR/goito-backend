package com.goito.springboot.service;

import com.goito.springboot.entity.User;
import com.goito.springboot.entity.User.AuthProvider;
import com.goito.springboot.entity.UserAddress;
import com.goito.springboot.repository.UserAddressRepository;
import com.goito.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAddressRepository userAddressRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        
        // Set default auth provider if not specified
        if (user.getAuthProvider() == null) {
            user.setAuthProvider(AuthProvider.LOCAL);
        }
        
        // Encode password if it's a local account
        if (user.getAuthProvider() == AuthProvider.LOCAL && user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        // Set timestamp fields
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        return userRepository.save(user);
    }

    public User updateUser(UUID id, User updatedUserData) {
        return userRepository.findById(id).map(user -> {
            if (updatedUserData.getName() != null) {
                user.setName(updatedUserData.getName());
            }
            
            // Don't allow email change if it's already taken by another user
            if (updatedUserData.getEmail() != null && 
                !user.getEmail().equals(updatedUserData.getEmail()) && 
                !userRepository.existsByEmail(updatedUserData.getEmail())) {
                user.setEmail(updatedUserData.getEmail());
            }
            
            if (updatedUserData.getGender() != null) {
                user.setGender(updatedUserData.getGender());
            }
            
            if (updatedUserData.getDateOfBirth() != null) {
                user.setDateOfBirth(updatedUserData.getDateOfBirth());
            }
            
            if (updatedUserData.getPhoneNumber() != null) {
                user.setPhoneNumber(updatedUserData.getPhoneNumber());
            }
            
            if (updatedUserData.getPreferredSize() != null) {
                user.setPreferredSize(updatedUserData.getPreferredSize());
            }
            
            // Only update password for local accounts and if password is provided
            if (user.getAuthProvider() == AuthProvider.LOCAL && 
                updatedUserData.getPassword() != null && 
                !updatedUserData.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUserData.getPassword()));
            }
            
            // Update timestamp
            user.setUpdatedAt(LocalDateTime.now());
            
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> getUser(UUID id) {
        return userRepository.findById(id);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User processOAuthUser(String email, String name, AuthProvider provider, String providerId) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        LocalDateTime now = LocalDateTime.now();
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            // Update provider details if user exists
            if (provider == AuthProvider.GOOGLE) {
                user.setGoogleId(providerId);
            } else if (provider == AuthProvider.FACEBOOK) {
                user.setFacebookId(providerId);
            }
            
            // Update auth provider if it was LOCAL before
            if (user.getAuthProvider() == AuthProvider.LOCAL) {
                user.setAuthProvider(provider);
            }
            
            // Update timestamp
            user.setUpdatedAt(now);
            
            return userRepository.save(user);
        } else {
            // Create new user if not exists
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setAuthProvider(provider);
            
            if (provider == AuthProvider.GOOGLE) {
                user.setGoogleId(providerId);
            } else if (provider == AuthProvider.FACEBOOK) {
                user.setFacebookId(providerId);
            }
            
            // Set timestamp fields
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            
            return userRepository.save(user);
        }
    }
    
    // User Address Management
    
    public List<UserAddress> getUserAddresses(UUID userId) {
        return userAddressRepository.findByUserUserIdOrderByIsDefaultDesc(userId);
    }
    
    @Transactional
    public UserAddress addUserAddress(UUID userId, UserAddress address) {
        return userRepository.findById(userId).map(user -> {
            address.setUser(user);
            
            // If this is the first address or marked as default, ensure it's set as default
            if (address.getIsDefault() || userAddressRepository.findByUserUserId(userId).isEmpty()) {
                address.setIsDefault(true);
                
                // If this is a new default address, unset default flag on all other addresses
                if (address.getIsDefault()) {
                    userAddressRepository.findByUserUserId(userId).forEach(existingAddress -> {
                        if (!existingAddress.getAddressId().equals(address.getAddressId())) {
                            existingAddress.setIsDefault(false);
                            userAddressRepository.save(existingAddress);
                        }
                    });
                }
            }
            
            return userAddressRepository.save(address);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public UserAddress updateUserAddress(UUID userId, UUID addressId, UserAddress updatedAddress) {
        return userAddressRepository.findById(addressId).map(address -> {
            if (!address.getUser().getUserId().equals(userId)) {
                throw new RuntimeException("Address does not belong to user");
            }
            
            if (updatedAddress.getAddressLine1() != null) {
                address.setAddressLine1(updatedAddress.getAddressLine1());
            }
            
            if (updatedAddress.getAddressLine2() != null) {
                address.setAddressLine2(updatedAddress.getAddressLine2());
            }
            
            if (updatedAddress.getCity() != null) {
                address.setCity(updatedAddress.getCity());
            }
            
            if (updatedAddress.getState() != null) {
                address.setState(updatedAddress.getState());
            }
            
            if (updatedAddress.getZipCode() != null) {
                address.setZipCode(updatedAddress.getZipCode());
            }
            
            if (updatedAddress.getCountry() != null) {
                address.setCountry(updatedAddress.getCountry());
            }
            
            // Handle default address setting
            if (updatedAddress.getIsDefault() != null && updatedAddress.getIsDefault()) {
                address.setIsDefault(true);
                
                // Unset default flag on all other addresses
                userAddressRepository.findByUserUserId(userId).forEach(existingAddress -> {
                    if (!existingAddress.getAddressId().equals(addressId)) {
                        existingAddress.setIsDefault(false);
                        userAddressRepository.save(existingAddress);
                    }
                });
            }
            
            return userAddressRepository.save(address);
        }).orElseThrow(() -> new RuntimeException("Address not found"));
    }
    
    public void deleteUserAddress(UUID userId, UUID addressId) {
        userAddressRepository.findById(addressId).ifPresent(address -> {
            if (!address.getUser().getUserId().equals(userId)) {
                throw new RuntimeException("Address does not belong to user");
            }
            
            userAddressRepository.delete(address);
            
            // If the deleted address was the default, set another address as default if available
            if (address.getIsDefault()) {
                userAddressRepository.findByUserUserId(userId).stream().findFirst().ifPresent(newDefault -> {
                    newDefault.setIsDefault(true);
                    userAddressRepository.save(newDefault);
                });
            }
        });
    }
}
