package com.example.security_first_touch.service;

import com.example.security_first_touch.domain.dto.RegistrationUserDto;
import com.example.security_first_touch.domain.entity.User;
import com.example.security_first_touch.domain.repository.RoleRepository;
import com.example.security_first_touch.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByName(username);
    }

    /**
     * Метод для преобразования в спрингового пользователя
     *
     * @param name имя пользователя
     * @return
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String name) {
        log.info("loadUserByUsername");
        log.info("username: {}", name);
        User user = findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(name)));

        //сделать маппер
        return new org.springframework.security.core.userdetails.User(
                user.getName(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );
    }

    public User createUser(RegistrationUserDto dto) {
        log.info("createUser");
        //проверка существования - если есть, то выкидывать ошибку
        User user = new User();
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setName(dto.name());
        user.setRoles(List.of(roleService.findRoleByName("ROLE_USER")));
        return userRepository.save(user);
    }
}

