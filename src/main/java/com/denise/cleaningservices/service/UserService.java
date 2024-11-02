package com.denise.cleaningservices.service;

import com.denise.cleaningservices.exception.ResourceNotFoundException;
import com.denise.cleaningservices.exception.UserAlreadyExistsException;
import com.denise.cleaningservices.model.Booking;
import com.denise.cleaningservices.model.Role;
import com.denise.cleaningservices.model.User;
import com.denise.cleaningservices.repository.BookingRepository;
import com.denise.cleaningservices.repository.RoleRepository;
import com.denise.cleaningservices.repository.UserRepository;
import com.denise.cleaningservices.response.UserResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        User theUser = getUser(email);
        if (theUser != null) {
            userRepository.deleteByEmail(email);
        }
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public List<String> findRolesByUserId(Long userId) {
        return roleRepository.findRolesByUserId(userId);
    }

    @Override
    public List<User> findUsersByRole(String role) {
        List<User> allUsers = userRepository.findAll();
        List<User> filteredUsers = new ArrayList<>();

        for (User user : allUsers) {
            for (Role userRole : user.getRoles()) {
                if (userRole.getName().equals(role)) {
                    filteredUsers.add(user);
                    break; // No need to check other roles for this user
                }
            }
        }

        return filteredUsers;
    }

    private UserResponse convertToUserResponse(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                roles
        );
    }

    public List<User> getAllWorkers() {
        return userRepository.findByRoleId(3L); // Fetch users with role_id = 3
    }



    @Override
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }


    public List<User> getAvailableWorkers(LocalDate startDate, LocalDate endDate) {
        List<User> allWorkers = getAllWorkers();
        List<User> availableWorkers = new ArrayList<>();

        for (User worker : allWorkers) {
            boolean isAvailable = true;
            for (Booking booking : worker.getBookings()) {
                if (!booking.getEndDate().isBefore(startDate) && !booking.getStartDate().isAfter(endDate)) {
                    isAvailable = false;
                    break;
                }
            }
            if (isAvailable) {
                availableWorkers.add(worker);
            }
        }
        return availableWorkers;
    }

}
