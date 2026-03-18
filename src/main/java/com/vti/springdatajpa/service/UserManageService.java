package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.dto.UserManagerDTO;

import java.util.List;

public interface UserManageService {
    List<User> getAllUsers();

    void lockUser(Integer id);
    void unlockUser(Integer id);

    UserManagerDTO updateUser(Integer id, UserManagerDTO userDto);

    void deleteUser(Integer id);
}
