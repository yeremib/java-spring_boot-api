package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.dto.request.AuthRequest;
import com.enigma.wmb_api.dto.response.LoginResponse;
import com.enigma.wmb_api.dto.response.RegisterResponse;
import com.enigma.wmb_api.entity.Role;
import com.enigma.wmb_api.entity.User;
import com.enigma.wmb_api.entity.UserCredential;
import com.enigma.wmb_api.repository.UserCredentialRepository;
import com.enigma.wmb_api.service.AuthService;
import com.enigma.wmb_api.service.JwtService;
import com.enigma.wmb_api.service.RoleService;
import com.enigma.wmb_api.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository userCredentialRepository;
    private final RoleService roleService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${wmb_api.email.superadmin}")
    private String superAdminEmail;

    @Value("${wmb_api.password.superadmin}")
    private String superAdminPassword;

    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initSuperAdmin() {
        Optional<UserCredential> currentUser = userCredentialRepository.findByEmail(superAdminEmail);
        if (currentUser.isPresent()) return;

        Role superAdmin = roleService.getOrSave(UserRole.ROLE_SUPER_ADMIN);
        Role admin = roleService.getOrSave(UserRole.ROLE_ADMIN);
        Role customer = roleService.getOrSave(UserRole.ROLE_CUSTOMER);

        UserCredential userSuperAdmin = UserCredential.builder()
                .email(superAdminEmail)
                .password(passwordEncoder.encode(superAdminPassword))
                .roles(List.of(superAdmin, admin, customer))
                .isEnable(true)
                .build();

        userCredentialRepository.save(userSuperAdmin);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse register(AuthRequest request) {
        Role role = roleService.getOrSave(UserRole.ROLE_CUSTOMER);
        String hashPassword = passwordEncoder.encode(request.getPassword());

        UserCredential newUserCredential = UserCredential.builder()
                .email(request.getEmail())
                .password(hashPassword)
                .roles(List.of(role))
                .isEnable(true)
                .build();

        userCredentialRepository.saveAndFlush(newUserCredential);

        String[] name = request.getEmail().split("@", 2);
        User newUser = User.builder()
                .name(name[0])
                .status(true)
                .userCredential(newUserCredential)
                .build();

        userService.create(newUser);

        List<String> roles = newUserCredential.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return RegisterResponse.builder()
                .email(newUserCredential.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    public RegisterResponse registerAdmin(AuthRequest request) {
        Role role = roleService.getOrSave(UserRole.ROLE_ADMIN);
        String hashPassword = passwordEncoder.encode(request.getPassword());

        UserCredential newUserCredential = UserCredential.builder()
                .email(request.getEmail())
                .password(hashPassword)
                .roles(List.of(role))
                .isEnable(true)
                .build();

        userCredentialRepository.saveAndFlush(newUserCredential);

        String[] name = request.getEmail().split("@", 2);
        User newUser = User.builder()
                .name(name[0])
                .status(true)
                .userCredential(newUserCredential)
                .build();

        userService.create(newUser);

        List<String> roles = newUserCredential.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return RegisterResponse.builder()
                .email(newUserCredential.getEmail())
                .roles(roles)
                .build();
    }

    @Override
    public LoginResponse login(AuthRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        UserCredential userCredential = (UserCredential) authenticate.getPrincipal();
        String token = jwtService.generateToken(userCredential);
        return LoginResponse.builder()
                .email(userCredential.getEmail())
                .roles(userCredential.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .token(token)
                .build();
    }
}
