package ohio.rizz.homeappliancestore.services;

import lombok.RequiredArgsConstructor;
import ohio.rizz.homeappliancestore.entities.User;
import ohio.rizz.homeappliancestore.enums.Role;
import ohio.rizz.homeappliancestore.exceptions.UserNotFoundException;
import ohio.rizz.homeappliancestore.repositories.UserRepository;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

    @Transactional
    public User createUser(String username, String rawPassword, Role role, String fullName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserNotFoundException("Пользователь с таким логином уже существует");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setFullName(fullName);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(UUID id, String newFullName, Role newRole, String newPassword) {
        User user = getUserById(id);
        if (newFullName != null) {
            user.setFullName(newFullName);
        }
        if (newRole != null) {
            user.setRole(newRole);
        }
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        return userRepository.save(user);
    }

    @Transactional
    public void blockUser(UUID id) {
        User user = getUserById(id);
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional
    public void unblockUser(UUID id) {
        User user = getUserById(id);
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}