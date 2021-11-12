package com.example.newsapi.service.impl;

import com.example.newsapi.dto.AddUserRolesDTO;
import com.example.newsapi.dto.UserDTO;
import com.example.newsapi.entity.Role;
import com.example.newsapi.entity.User;
import com.example.newsapi.exception.ResourceNotFoundException;
import com.example.newsapi.modelassembler.UserMapper;
import com.example.newsapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bcryptEncoder;

    @Mock
    RoleServiceImpl roleService;

    @Mock
    UserMapper userMapper;

    User user1;
    Role subscriberRole;
    Role journalistRole;
    Role adminRole;

    @BeforeEach
    void setup(){
        subscriberRole = new Role(1, "SUBSCRIBER");
        journalistRole = new Role(2, "JOURNALIST");
        adminRole = new Role(3, "ADMIN");
        Set<Role> roles1 = new HashSet<>(Collections.singleton(subscriberRole));
        user1 = new User(1, "user1", "encoded password", roles1, null);
    }

    @Test
    void whenSaveUserAndUserFound_thenUserWithEncodedPasswordAndDefaultRoleIsSaved() {
        UserDTO requestedUserDto = new UserDTO("username", "password");
        User user = new User(1, "username", "password", null, null);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(user);
        when(roleService.findByName(anyString())).thenReturn(subscriberRole);
        when(bcryptEncoder.encode(anyString())).thenReturn("encoded password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.save(requestedUserDto);

        verify(userRepository, times(1)).save(any(User.class));
        assertEquals(requestedUserDto.getUsername(), user.getUsername());
        assertEquals("encoded password", user.getPassword());
        assertEquals(user.getRoles().size(), 1);
        assertTrue(user.getRoles().contains(subscriberRole));
    }

    @Test
    void whenLoadUserByUsernameAndUserNotFound_thenUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(anyString()));
    }

    @Test
    void whenLoadUserByUsernameAndUserFound_thenReturnUserDetails(){
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));

        UserDetails userDetails = userService.loadUserByUsername(user1.getUsername());

        assertEquals(userDetails.getUsername(), user1.getUsername());
        assertEquals(userDetails.getPassword(), user1.getPassword());

        List<String> authoritiesList = userDetails.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());

        assertEquals(authoritiesList.size(), 1);
        assertTrue(authoritiesList.contains(subscriberRole.getName()));
    }

    @Test
    void whenAddRolesAndUserFound_thenOnlyNewRolesAreAdded() {
        AddUserRolesDTO additionalRolesDto = new AddUserRolesDTO(List.of("JOURNALIST", "ADMIN"));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(roleService.findByName(journalistRole.getName())).thenReturn(journalistRole);
        when(roleService.findByName(adminRole.getName())).thenReturn(adminRole);
        when(userRepository.save(any(User.class))).thenReturn(user1);

        String result = userService.addRoles(additionalRolesDto, 1);

        verify(userRepository, times(1)).save(any(User.class));
        verify(roleService, times(2)).findByName(anyString());
        assertEquals(3, user1.getRoles().size());
        assertTrue(user1.getRoles().contains(subscriberRole));
        assertTrue(user1.getRoles().contains(journalistRole));
        assertTrue(user1.getRoles().contains(adminRole));
        assertTrue(result.contains(subscriberRole.getName()));
        assertTrue(result.contains(journalistRole.getName()));
        assertTrue(result.contains(adminRole.getName()));
    }

    @Test
    void whenAddRolesAndUserNotFound_thenNotFoundException(){
        AddUserRolesDTO additionalRolesDto = new AddUserRolesDTO(List.of("JOURNALIST", "ADMIN"));
        assertThrows(ResourceNotFoundException.class, () -> userService.addRoles(additionalRolesDto, 1));
    }
}
