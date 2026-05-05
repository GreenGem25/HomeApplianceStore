package ohio.rizz.homeappliancestore.services;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.dto.UserCreateDto;
import ohio.rizz.homeappliancestore.dto.UserEditDto;
import ohio.rizz.homeappliancestore.entities.User;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.enums.Role;
import ohio.rizz.homeappliancestore.exceptions.UserNotFoundException;
import ohio.rizz.homeappliancestore.mappers.UserMapper;
import ohio.rizz.homeappliancestore.repositories.UserRepository;
import org.mapstruct.control.MappingControl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuditService auditService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public User createUser(UserCreateDto userCreateDto) {
        if (userRepository.findByUsername(userCreateDto.getUsername()).isPresent()) {
            throw new UserNotFoundException("Пользователь с таким логином уже существует");
        }
        User user = userMapper.toEntity(userCreateDto, passwordEncoder);
        User savedUser = userRepository.save(user);
        auditService.log(AuditAction.CREATE, AuditEntityType.USER, savedUser.getId().toString(),
                String.format("Created user '%s' with role %s", savedUser.getUsername(), savedUser.getRole()));
        return savedUser;
    }

    @Transactional
    public User updateUser(UUID id, UserEditDto userEditDto) {
        User user = getUserById(id);

        boolean passChanged = false;
        boolean roleChanged = false;

        if (userEditDto.getFullName() != null) {
            user.setFullName(userEditDto.getFullName());
        }
        if (userEditDto.getRole() != null) {
            if (userEditDto.getRole() == user.getRole()) {roleChanged = true;}
            user.setRole(userEditDto.getRole());
        }
        if (userEditDto.getPassword() != null && !userEditDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userEditDto.getPassword()));
            passChanged = true;
        }

        User updatedUser = userRepository.save(user);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Updated user '%s'", updatedUser.getUsername()));
        if (roleChanged) {
            stringBuilder.append(String.format(", role changed to '%s'", updatedUser.getRole()));
        }
        if (passChanged) {
            stringBuilder.append(String.format(", password changed to '%s'", updatedUser.getPassword()));
        }
        auditService.log(AuditAction.UPDATE, AuditEntityType.USER, id.toString(), stringBuilder.toString());

        return updatedUser;
    }

    @Transactional
    public void blockUser(UUID id) {
        User user = getUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
        auditService.log(AuditAction.BLOCK, AuditEntityType.USER, id.toString(),
                String.format("User '%s' blocked", user.getUsername()));
    }

    @Transactional
    public void unblockUser(UUID id) {
        User user = getUserById(id);
        user.setEnabled(true);
        userRepository.save(user);
        auditService.log(AuditAction.UNBLOCK, AuditEntityType.USER, id.toString(),
                String.format("User '%s' unblocked", user.getUsername()));
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
        auditService.log(AuditAction.DELETE, AuditEntityType.USER, id.toString(),
                String.format("User '%s' deleted", user.getUsername()));
    }
}