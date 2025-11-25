package com.needed.task.service.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.needed.task.dto.UserDTO;
import com.needed.task.exception.AppException;
import com.needed.task.exception.ResourceNotFoundException;
import com.needed.task.mapper.UserMapper;
import com.needed.task.model.Role;
import com.needed.task.model.User;
import com.needed.task.repository.RoleRepository;
import com.needed.task.repository.UserRepository;
import com.needed.task.service.UserService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
        .map(UserMapper::userToUserDto).toList();
    }
    @Override
    public UserDTO createUser(UserDTO userDto) {
        User user = UserMapper.userDtoToUser(userDto);

        // get role from db
        Role role = roleRepository.findByName(userDto.role()).orElseThrow(
                () -> new ResourceNotFoundException( "Role not found")
        );

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(userDto.password()));

        return UserMapper.userToUserDto(userRepository.save(user));
    }
    @Override
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );
        return UserMapper.userToUserDto(user);
    }
    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );
        return UserMapper.userToUserDto(user);
    }
    @Override
    public UserDTO updateUser(Long userId, UserDTO userDto) {
        // get user from db
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );

        // get role from db
        Role role = roleRepository.findByName(userDto.role()).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "Role not found")
        );

        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRole(role);

        return UserMapper.userToUserDto(userRepository.save(user));
    }
    @Override
    public String deleteUser(Long userId) {
        // get user from db
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );

        userRepository.delete(user);

        return String.format("User with %d deleted successfully", userId);
    }
    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
}