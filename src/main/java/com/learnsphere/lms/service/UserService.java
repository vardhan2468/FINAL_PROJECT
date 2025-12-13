package com.learnsphere.lms.service;

import com.learnsphere.lms.model.User;
import com.learnsphere.lms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Save a new user to the database
     * 
     * @param user the user to save
     * @return the saved user
     */
    public User saveUser(User user) {
        // Encrypt password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Fetch all users from the database
     * 
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Fetch a user by their email address
     * 
     * @param email the email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Fetch a user by their ID
     * 
     * @param id the user ID
     * @return Optional containing the user if found, empty otherwise
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Check if a user exists with the given email
     * 
     * @param email the email address to check
     * @return true if user exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Delete a user by ID
     * 
     * @param id the user ID to delete
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Get total count of users
     * 
     * @return the number of users
     */
    public long getUserCount() {
        return userRepository.count();
    }
}
