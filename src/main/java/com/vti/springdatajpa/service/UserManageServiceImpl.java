package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.dto.UserManagerDTO;
import com.vti.springdatajpa.repository.UserManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserManageServiceImpl implements  UserManageService {
    private final UserManagerRepository userManagerRepository;

    public UserManageServiceImpl(UserManagerRepository userManagerRepository) {
        this.userManagerRepository = userManagerRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userManagerRepository.findByRoleNot(Role.ADMIN);
    }

    @Override
    public void lockUser(Integer id) {
        userManagerRepository.lockUser(id);
    }

    @Override
    public void unlockUser(Integer id) {
        userManagerRepository.unlockUser(id);
    }

    @Override
    public UserManagerDTO updateUser(Integer id, UserManagerDTO userDto) {
        User user = userManagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setFullName(userDto.getFullName());
        user.setActive(userDto.isActive());

        User savedUser = userManagerRepository.save(user);

        UserManagerDTO updatedDto = new UserManagerDTO();
        updatedDto.setId(savedUser.getId());
        updatedDto.setUserName(savedUser.getUserName());
        updatedDto.setEmail(savedUser.getEmail());
        updatedDto.setPhone(savedUser.getPhone());
        updatedDto.setFullName(savedUser.getFullName());
        updatedDto.setActive(savedUser.isActive());
        updatedDto.setCreatedAt(savedUser.getCreatedAt());

        return updatedDto;
    }
}
