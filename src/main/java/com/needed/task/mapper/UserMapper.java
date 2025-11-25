package com.needed.task.mapper;

import java.util.Collections;
import java.util.stream.Collectors;

import com.needed.task.dto.UserDTO;
import com.needed.task.dto.UserLoggedDto;
import com.needed.task.model.Permission;
import com.needed.task.model.User;

public class UserMapper {
    public static UserDTO userToUserDto(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole().getAuthority(), // ROLE_ADMIN, ROLE_MANAGER, etc.
                Collections.emptySet() // Временное решение - пустые permissions
        );
    }
    
    public static User userDtoToUser(UserDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        return user;
    }
    public static UserLoggedDto userToUserLoggedDto(User user) {
        return new UserLoggedDto(
                user.getUsername(),
                user.getRole().getAuthority(),
                user.getRole().getPermissions().stream().map(Permission::getAuthority).collect(Collectors.toSet())
        );
    }

}
