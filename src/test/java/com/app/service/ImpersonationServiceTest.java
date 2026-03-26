package com.app.service;

import com.app.entity.ImpersonationLog;
import com.app.entity.User;
import com.app.repository.ImpersonationLogRepository;
import com.app.repository.UserRepository;
import com.app.util.JwtUtil;
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
class ImpersonationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImpersonationLogRepository impersonationLogRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ImpersonationService impersonationService;

    private User adminUser;
    private User tutoredUser;
    private User regularUser;
    private UUID adminId;
    private UUID tutoredUserId;
    private UUID regularUserId;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        tutoredUserId = UUID.randomUUID();
        regularUserId = UUID.randomUUID();

        adminUser = User.builder()
                .id_user(adminId)
                .email("admin@test.com")
                .role(User.Role.ADMIN)
                .is_tutored(false)
                .build();

        tutoredUser = User.builder()
                .id_user(tutoredUserId)
                .email("tutored@test.com")
                .role(User.Role.USER)
                .is_tutored(true)
                .build();

        regularUser = User.builder()
                .id_user(regularUserId)
                .email("user@test.com")
                .role(User.Role.USER)
                .is_tutored(false)
                .build();
    }

    @Test
    void testGenerateImpersonationToken_Success() {
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(tutoredUserId)).thenReturn(Optional.of(tutoredUser));
        when(jwtUtil.createCustomToken(any(), any(), any())).thenReturn("impersonation-token");

        String token = impersonationService.generateImpersonationToken(adminId, tutoredUserId);

        assertNotNull(token);
        assertEquals("impersonation-token", token);
        verify(userRepository, times(1)).findById(adminId);
        verify(userRepository, times(1)).findById(tutoredUserId);
        verify(jwtUtil, times(1)).createCustomToken(any(), any(), any());
    }

    @Test
    void testGenerateImpersonationToken_AdminNotFound() {
        when(userRepository.findById(adminId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            impersonationService.generateImpersonationToken(adminId, tutoredUserId);
        });

        verify(userRepository, times(1)).findById(adminId);
        verify(userRepository, never()).findById(tutoredUserId);
    }

    @Test
    void testGenerateImpersonationToken_TargetUserNotFound() {
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(tutoredUserId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            impersonationService.generateImpersonationToken(adminId, tutoredUserId);
        });

        verify(userRepository, times(1)).findById(adminId);
        verify(userRepository, times(1)).findById(tutoredUserId);
    }

    @Test
    void testGenerateImpersonationToken_InitiatorNotAdmin() {
        User nonAdminUser = User.builder()
                .id_user(adminId)
                .role(User.Role.USER)
                .build();

        when(userRepository.findById(adminId)).thenReturn(Optional.of(nonAdminUser));
        when(userRepository.findById(tutoredUserId)).thenReturn(Optional.of(tutoredUser));

        assertThrows(RuntimeException.class, () -> {
            impersonationService.generateImpersonationToken(adminId, tutoredUserId);
        });

        verify(userRepository, times(1)).findById(adminId);
        verify(userRepository, times(1)).findById(tutoredUserId);
    }

    @Test
    void testGenerateImpersonationToken_TargetNotTutored() {
        when(userRepository.findById(adminId)).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(regularUserId)).thenReturn(Optional.of(regularUser));

        assertThrows(RuntimeException.class, () -> {
            impersonationService.generateImpersonationToken(adminId, regularUserId);
        });

        verify(userRepository, times(1)).findById(adminId);
        verify(userRepository, times(1)).findById(regularUserId);
    }

    @Test
    void testLogImpersonationAction() {
        when(impersonationLogRepository.save(any(ImpersonationLog.class))).thenReturn(new ImpersonationLog());

        impersonationService.logImpersonationAction(
                adminId,
                tutoredUserId,
                "TEST_ACTION",
                "/test/endpoint",
                "GET",
                "127.0.0.1",
                "Test-Agent",
                "request-body"
        );

        verify(impersonationLogRepository, times(1)).save(any(ImpersonationLog.class));
    }

    @Test
    void testIsImpersonationToken_Valid() {
        when(jwtUtil.extractClaim(any(), any())).thenReturn("impersonation");

        boolean result = impersonationService.isImpersonationToken("test-token");

        assertTrue(result);
        verify(jwtUtil, times(1)).extractClaim(any(), any());
    }

    @Test
    void testIsImpersonationToken_Invalid() {
        when(jwtUtil.extractClaim(any(), any())).thenThrow(new RuntimeException("Invalid token"));

        boolean result = impersonationService.isImpersonationToken("invalid-token");

        assertFalse(result);
        verify(jwtUtil, times(1)).extractClaim(any(), any());
    }

    @Test
    void testGetImpersonatedBy() {
        String adminIdStr = adminId.toString();
        when(jwtUtil.extractClaim(any(), any())).thenReturn(adminIdStr);

        UUID result = impersonationService.getImpersonatedBy("test-token");

        assertEquals(adminId, result);
        verify(jwtUtil, times(1)).extractClaim(any(), any());
    }

    @Test
    void testGetImpersonatedBy_InvalidToken() {
        when(jwtUtil.extractClaim(any(), any())).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class, () -> {
            impersonationService.getImpersonatedBy("invalid-token");
        });

        verify(jwtUtil, times(1)).extractClaim(any(), any());
    }

    @Test
    void testGetImpersonatedUserId() {
        String tutoredUserIdStr = tutoredUserId.toString();
        when(jwtUtil.extractClaim(any(), any())).thenReturn(tutoredUserIdStr);

        UUID result = impersonationService.getImpersonatedUserId("test-token");

        assertEquals(tutoredUserId, result);
        verify(jwtUtil, times(1)).extractClaim(any(), any());
    }

    @Test
    void testGetImpersonatedUserId_InvalidToken() {
        when(jwtUtil.extractClaim(any(), any())).thenThrow(new RuntimeException("Invalid token"));

        assertThrows(RuntimeException.class, () -> {
            impersonationService.getImpersonatedUserId("invalid-token");
        });

        verify(jwtUtil, times(1)).extractClaim(any(), any());
    }
}
