package com.app.service;

import com.app.entity.User;
import com.app.exception.LastAdminException;
import com.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User adminUser;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        adminUser = User.builder()
                .id_user(adminId)
                .nom("Admin")
                .prenom("User")
                .email("admin@test.com")
                .role(User.Role.ADMIN)
                .statut(User.Statut.ACTIF)
                .build();
    }

    @Test
    void testCannotDeleteLastAdmin() {
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.countByRole(User.Role.ADMIN)).thenReturn(1L);

        LastAdminException exception = assertThrows(LastAdminException.class, () -> {
            userService.delete(adminId);
        });

        assertEquals("cannot remove last admin", exception.getMessage());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void testCanDeleteAdminWhenMultipleExist() {
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.countByRole(User.Role.ADMIN)).thenReturn(2L);

        assertDoesNotThrow(() -> userService.delete(adminId));
        verify(userRepository, times(1)).deleteById(adminId);
    }

    @Test
    void testCannotDowngradeLastAdmin() {
        User updatedUser = User.builder()
                .role(User.Role.USER)
                .nom("Admin")
                .prenom("User")
                .email("admin@test.com")
                .statut(User.Statut.ACTIF)
                .build();

        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.countByRole(User.Role.ADMIN)).thenReturn(1L);

        LastAdminException exception = assertThrows(LastAdminException.class, () -> {
            userService.update(adminId, updatedUser);
        });

        assertEquals("cannot remove last admin", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCanDowngradeAdminWhenMultipleExist() {
        User updatedUser = User.builder()
                .role(User.Role.USER)
                .nom("Admin")
                .prenom("User")
                .email("admin@test.com")
                .statut(User.Statut.ACTIF)
                .build();

        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.countByRole(User.Role.ADMIN)).thenReturn(2L);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        assertDoesNotThrow(() -> userService.update(adminId, updatedUser));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCanDeleteNonAdminUser() {
        UUID userId = UUID.randomUUID();
        User regularUser = User.builder()
                .id_user(userId)
                .nom("Regular")
                .prenom("User")
                .email("user@test.com")
                .role(User.Role.USER)
                .statut(User.Statut.ACTIF)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(regularUser));

        assertDoesNotThrow(() -> userService.delete(userId));
        verify(userRepository, times(1)).deleteById(userId);
    }
}
