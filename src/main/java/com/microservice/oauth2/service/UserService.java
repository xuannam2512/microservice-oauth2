package com.microservice.oauth2.service;

import com.microservice.oauth2.domain.Permission;
import com.microservice.oauth2.domain.RolePermission;
import com.microservice.oauth2.domain.User;
import com.microservice.oauth2.domain.bean.UserDto;
import com.microservice.oauth2.domain.mapper.UserMapper;
import com.microservice.oauth2.repository.RolePermissionRepository;
import com.microservice.oauth2.repository.RoleRepository;
import com.microservice.oauth2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Transactional
    public UserDto create(UserDto userDto) {
        var user = mapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(roleRepository.getOne(userDto.getRoleId()));

        return mapper.toUserDto(repository.save(user));
    }

    @Transactional
    public UserDto update(Integer id, UserDto userDto) {
        var user = repository.getOne(id);
        mapper.update(user, userDto);
        user.setRole(roleRepository.getOne(userDto.getRoleId()));

        return mapper.toUserDto(repository.save(user));
    }

    @Transactional(readOnly = true)
    public UserDto getById(Integer id) {
        return mapper.toUserDto(repository.getOne(id));
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermissionByUserId(Integer userId) {
        var user = repository.getOne(userId);
        return rolePermissionRepository.findAllByRole(user.getRole())
                .stream().map(RolePermission::getPermission)
                .collect(Collectors.toList());
    }

    @Transactional
    public User getUserByUsername(String username) {
        return repository.findFirstByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
