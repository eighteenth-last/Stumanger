package com.student.service;

import com.student.common.exception.BusinessException;
import com.student.dto.LoginRequest;
import com.student.dto.LoginResponse;
import com.student.entity.User;
import com.student.entity.enums.UserRole;
import com.student.mapper.UserMapper;
import com.student.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 认证服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRealName("测试用户");
        testUser.setRole(UserRole.STUDENT);
        testUser.setStatus(true);

        loginRequest = new LoginRequest("testuser", "password");
    }

    @Test
    void testLoginSuccess() {
        // 模拟数据
        when(userMapper.findByUsernameAndStatus("testuser", true)).thenReturn(testUser);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        // 执行测试
        LoginResponse response = authService.login(loginRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals("测试用户", response.getRealName());
        assertEquals("student", response.getRole());
        assertNotNull(response.getToken());
    }

    @Test
    void testLoginUserNotFound() {
        // 模拟数据
        when(userMapper.findByUsernameAndStatus("testuser", true)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("用户名或密码错误", exception.getMessage());
    }

    @Test
    void testLoginWrongPassword() {
        // 模拟数据
        when(userMapper.findByUsernameAndStatus("testuser", true)).thenReturn(testUser);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("用户名或密码错误", exception.getMessage());
    }
}