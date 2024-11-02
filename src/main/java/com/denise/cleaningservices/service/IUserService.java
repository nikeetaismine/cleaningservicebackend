package com.denise.cleaningservices.service;

import com.denise.cleaningservices.model.User;

import java.time.LocalDate;
import java.util.List;

public interface IUserService {
    User registerUser(User user);
    List<User> getUsers();
    void deleteUser(String email);
    User getUser(String email);
    List<String> findRolesByUserId(Long userId);
    List<User> findUsersByRole(String role);
    User getUserProfile(Long userId);
    List<User> getAvailableWorkers(LocalDate start, LocalDate end);
    List<User> getAllWorkers();
}

