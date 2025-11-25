package com.needed.task.service;

import java.util.List;
import com.needed.task.dto.UserDTO;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long id);
    UserDTO getUserByUsername(String username);
    UserDTO createUser(UserDTO userDto);
    UserDTO updateUser(Long id, UserDTO userDto);
    String deleteUser(Long id);
    boolean userExists(String username);
}
