package com.student.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.exception.BusinessException;
import com.student.dto.*;
import com.student.entity.User;
import com.student.entity.enums.UserRole;
import com.student.mapper.UserMapper;
import com.student.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;
    private PasswordChangeRequest passwordChangeRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setRole(UserRole.STUDENT);
        testUser.setStatus(true);
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());

        createRequest = new UserCreateRequest();
        createRequest.setUsername("newuser");
        createRequest.setPassword("password123");
        createRequest.setRealName("新用户");
        createRequest.setEmail("newuser@example.com");
        createRequest.setPhone("13900139000");
        createRequest.setRole(UserRole.STUDENT);

        updateRequest = new UserUpdateRequest();
        updateRequest.setRealName("更新用户");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhone("13700137000");
        updateRequest.setRole(UserRole.TEACHER);
        updateRequest.setStatus(true);

        passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setOldPassword("oldPassword");
        passwordChangeRequest.setNewPassword("newPassword123");
        passwordChangeRequest.setConfirmPassword("newPassword123");
    }

    @Test
    void testCreateUserSuccess() {
        // 模拟数据
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L); // 用户名和邮箱不存在
        when(passwordEncoder.encode("password123")).thenReturn("encodedNewPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        // 执行测试
        UserResponse response = userService.createUser(createRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
        assertEquals("新用户", response.getRealName());
        assertEquals("newuser@example.com", response.getEmail());
        assertEquals(UserRole.STUDENT, response.getRole());
        assertTrue(response.getStatus());

        // 验证方法调用
        verify(userMapper, times(2)).selectCount(any(QueryWrapper.class)); // 检查用户名和邮箱
        verify(passwordEncoder).encode("password123");
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void testCreateUserDuplicateUsername() {
        // 模拟用户名已存在
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(createRequest);
        });

        assertEquals("用户名 'newuser' 已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testCreateUserDuplicateEmail() {
        // 模拟用户名不存在但邮箱已存在
        when(userMapper.selectCount(any(QueryWrapper.class)))
                .thenReturn(0L) // 用户名不存在
                .thenReturn(1L); // 邮箱已存在

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.createUser(createRequest);
        });

        assertEquals("邮箱 'newuser@example.com' 已存在", exception.getMessage());
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    void testGetUserByIdSuccess() {
        // 模拟数据
        when(userMapper.selectById(1L)).thenReturn(testUser);

        // 执行测试
        UserResponse response = userService.getUserById(1L);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("测试用户", response.getRealName());
        assertEquals(UserRole.STUDENT, response.getRole());

        verify(userMapper).selectById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        // 模拟用户不存在
        when(userMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserById(1L);
        });

        assertEquals("用户不存在，ID: 1", exception.getMessage());
    }

    @Test
    void testGetUserByUsernameSuccess() {
        // 模拟数据
        when(userMapper.findByUsername("testuser")).thenReturn(testUser);

        // 执行测试
        UserResponse response = userService.getUserByUsername("testuser");

        // 验证结果
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("测试用户", response.getRealName());

        verify(userMapper).findByUsername("testuser");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        // 模拟用户不存在
        when(userMapper.findByUsername("nonexistent")).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.getUserByUsername("nonexistent");
        });

        assertEquals("用户不存在，用户名: nonexistent", exception.getMessage());
    }

    @Test
    void testUpdateUserSuccess() {
        // 模拟数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L); // 邮箱不重复
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        UserResponse response = userService.updateUser(1L, updateRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals("更新用户", response.getRealName());
        assertEquals("updated@example.com", response.getEmail());
        assertEquals(UserRole.TEACHER, response.getRole());

        verify(userMapper).selectById(1L);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        // 模拟用户不存在
        when(userMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.updateUser(1L, updateRequest);
        });

        assertEquals("用户不存在，ID: 1", exception.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void testDeleteUserSuccess() {
        // 模拟数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.deleteById(1L)).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userMapper).selectById(1L);
        verify(userMapper).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        // 模拟用户不存在
        when(userMapper.selectById(1L)).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.deleteUser(1L);
        });

        assertEquals("用户不存在，ID: 1", exception.getMessage());
        verify(userMapper, never()).deleteById(1L);
    }

    @Test
    void testGetUsersByRole() {
        // 模拟数据
        List<User> users = Arrays.asList(testUser);
        when(userMapper.selectList(any(QueryWrapper.class))).thenReturn(users);

        // 执行测试
        List<UserResponse> responses = userService.getUsersByRole(UserRole.STUDENT);

        // 验证结果
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("testuser", responses.get(0).getUsername());
        assertEquals(UserRole.STUDENT, responses.get(0).getRole());

        verify(userMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    void testChangePasswordSuccess() {
        // 模拟数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.changePassword(1L, passwordChangeRequest));

        verify(userMapper).selectById(1L);
        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword123");
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testChangePasswordMismatch() {
        // 设置密码不匹配
        passwordChangeRequest.setConfirmPassword("differentPassword");

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword(1L, passwordChangeRequest);
        });

        assertEquals("新密码和确认密码不匹配", exception.getMessage());
        verify(userMapper, never()).selectById(any());
    }

    @Test
    void testChangePasswordWrongOldPassword() {
        // 模拟数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(false);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.changePassword(1L, passwordChangeRequest);
        });

        assertEquals("旧密码不正确", exception.getMessage());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void testResetPasswordSuccess() {
        // 模拟数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.resetPassword(1L, "newPassword"));

        verify(userMapper).selectById(1L);
        verify(passwordEncoder).encode("newPassword");
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testUpdateUserStatusSuccess() {
        // 模拟数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.updateUserStatus(1L, false));

        verify(userMapper).selectById(1L);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testAssignRoleSuccess() {
        // 模拟数据
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // 执行测试
        assertDoesNotThrow(() -> userService.assignRole(1L, UserRole.ADMIN));

        verify(userMapper).selectById(1L);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testExistsByUsername() {
        // 模拟数据
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // 执行测试
        boolean exists = userService.existsByUsername("testuser");

        // 验证结果
        assertTrue(exists);
        verify(userMapper).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testExistsByEmail() {
        // 模拟数据
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // 执行测试
        boolean exists = userService.existsByEmail("test@example.com");

        // 验证结果
        assertTrue(exists);
        verify(userMapper).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testExistsByEmailEmpty() {
        // 执行测试
        boolean exists = userService.existsByEmail("");

        // 验证结果
        assertFalse(exists);
        verify(userMapper, never()).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testValidatePassword() {
        // 模拟数据
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        // 执行测试
        boolean isValid = userService.validatePassword(testUser, "rawPassword");

        // 验证结果
        assertTrue(isValid);
        verify(passwordEncoder).matches("rawPassword", "encodedPassword");
    }
}