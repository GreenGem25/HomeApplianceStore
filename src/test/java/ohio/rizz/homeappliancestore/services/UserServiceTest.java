package ohio.rizz.homeappliancestore.services;

import ohio.rizz.homeappliancestore.dto.UserCreateDto;
import ohio.rizz.homeappliancestore.dto.UserEditDto;
import ohio.rizz.homeappliancestore.entities.User;
import ohio.rizz.homeappliancestore.enums.AuditAction;
import ohio.rizz.homeappliancestore.enums.AuditEntityType;
import ohio.rizz.homeappliancestore.enums.Role;
import ohio.rizz.homeappliancestore.exceptions.UserNotFoundException;
import ohio.rizz.homeappliancestore.mappers.UserMapper;
import ohio.rizz.homeappliancestore.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    // Тестовые данные
    private final UUID USER_ID = UUID.randomUUID();
    private final String USERNAME = "testuser";
    private final String RAW_PASSWORD = "rawPassword";
    private final String ENCODED_PASSWORD = "$2a$10$...encoded...";
    private final String FULL_NAME = "Test User";

    private User sampleUser;
    private UserCreateDto userCreateDto;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(USER_ID);
        sampleUser.setUsername(USERNAME);
        sampleUser.setPassword(ENCODED_PASSWORD);
        sampleUser.setRole(Role.MANAGER);
        sampleUser.setFullName(FULL_NAME);
        sampleUser.setEnabled(true);

        userCreateDto = new UserCreateDto();
        userCreateDto.setUsername(USERNAME);
        userCreateDto.setPassword(RAW_PASSWORD);
        userCreateDto.setRole(Role.MANAGER);
        userCreateDto.setFullName(FULL_NAME);
    }

    // ================== Тесты создания пользователя ==================

    @Test
    void createUser_Success() {
        // given (предусловия)
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        // UserMapper.toEntity принимает (UserCreateDto, PasswordEncoder)
        when(userMapper.toEntity(eq(userCreateDto), any(PasswordEncoder.class))).thenReturn(sampleUser);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        // when (действие)
        User result = userService.createUser(userCreateDto);

        // then (проверки)
        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(ENCODED_PASSWORD, result.getPassword());
        assertEquals(Role.MANAGER, result.getRole());

        // Проверяем, что аудит был вызван с правильными параметрами
        verify(auditService, times(1)).log(
                eq(AuditAction.CREATE),
                eq(AuditEntityType.USER),
                eq(USER_ID.toString()),
                contains("Created user")
        );
    }

    @Test
    void createUser_DuplicateUsername_ThrowsException() {
        // given
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(sampleUser));

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.createUser(userCreateDto));

        // Проверяем, что save() ни разу не вызван
        verify(userRepository, never()).save(any());
        // и аудит не записан
        verify(auditService, never()).log(any(), any(), any(), any());
    }

    // ================== Тесты получения пользователей ==================

    @Test
    void getAllUsers_ReturnsList() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(USERNAME, users.get(0).getUsername());
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));

        User user = userService.getUserById(USER_ID);

        assertNotNull(user);
        assertEquals(USER_ID, user.getId());
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(USER_ID));
    }

    // ================== Тесты обновления ==================

    @Test
    void updateUser_ChangeFullNameAndRole() {
        UserEditDto editDto = new UserEditDto();
        editDto.setFullName("New Name");
        editDto.setRole(Role.ADMIN);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User updated = userService.updateUser(USER_ID, editDto);

        assertEquals("New Name", updated.getFullName());
        assertEquals(Role.ADMIN, updated.getRole());
        verify(auditService).log(eq(AuditAction.UPDATE), eq(AuditEntityType.USER), eq(USER_ID.toString()), anyString());
    }

    @Test
    void updateUser_ChangePassword() {
        UserEditDto editDto = new UserEditDto();
        editDto.setFullName(FULL_NAME);
        editDto.setRole(Role.MANAGER);
        String newRaw = "newPass";
        editDto.setPassword(newRaw);
        String newEncoded = "$2a$10$...newEncoded...";

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.encode(newRaw)).thenReturn(newEncoded);
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        User updated = userService.updateUser(USER_ID, editDto);

        assertEquals(newEncoded, updated.getPassword());
        verify(passwordEncoder).encode(newRaw);
    }

    // ================== Тесты блокировки / разблокировки ==================

    @Test
    void blockUser_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        userService.blockUser(USER_ID);

        assertFalse(sampleUser.isEnabled());
        verify(auditService).log(eq(AuditAction.BLOCK), eq(AuditEntityType.USER), eq(USER_ID.toString()), anyString());
    }

    @Test
    void unblockUser_Success() {
        sampleUser.setEnabled(false);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        userService.unblockUser(USER_ID);

        assertTrue(sampleUser.isEnabled());
        verify(auditService).log(eq(AuditAction.UNBLOCK), eq(AuditEntityType.USER), eq(USER_ID.toString()), anyString());
    }

    // ================== Тесты удаления ==================

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser));

        userService.deleteUser(USER_ID);

        verify(userRepository).delete(sampleUser);
        verify(auditService).log(eq(AuditAction.DELETE), eq(AuditEntityType.USER), eq(USER_ID.toString()), anyString());
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(USER_ID));
        verify(userRepository, never()).delete(any());
    }
}