package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.dto.*;
import com.student.entity.enums.UserRole;
import com.student.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器测试类
 */
@SpringBootTest
@AutoConfigureWebMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse testUserResponse;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUserResponse = new UserResponse();
        testUserResponse.setId(1L);
        testUserResponse.setUsername("testuser");
        testUserResponse.setRealName("测试用户");
        testUserResponse.setEmail("test@example.com");
        testUserResponse.setPhone("13800138000");
        testUserResponse.setRole(UserRole.STUDENT);
        testUserResponse.setStatus(true);
        testUserResponse.setCreateTime(LocalDateTime.now());
        testUserResponse.setUpdateTime(LocalDateTime.now());

        createRequest = new UserCreateRequest();
        createRequest.setUsername("newuser");
        createRequest.setPassword("password123");
        createRequest.setRealName("新用户");
        createRequest.setEmail("newuser@example.com");
        createRequest.setRole(UserRole.STUDENT);

        updateRequest = new UserUpdateRequest();
        updateRequest.setRealName("更新用户");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setRole(UserRole.TEACHER);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsers() throws Exception {
        // 模拟分页数据
        Page<UserResponse> page = new Page<>();
        page.setRecords(Arrays.asList(testUserResponse));
        page.setTotal(1);
        page.setCurrent(1);
        page.setSize(10);

        when(userService.getUsers(any(Page.class), anyString(), any(UserRole.class), any(Boolean.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("current", "1")
                        .param("size", "10")
                        .param("username", "test")
                        .param("role", "STUDENT")
                        .param("status", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询成功"))
                .andExpect(jsonPath("$.data.records[0].username").value("testuser"));

        verify(userService).getUsers(any(Page.class), eq("test"), eq(UserRole.STUDENT), eq(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        List<UserResponse> users = Arrays.asList(testUserResponse);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].username").value("testuser"));

        verify(userService).getAllUsers();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserResponse);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));

        verify(userService).getUserById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateUser() throws Exception {
        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(testUserResponse);

        mockMvc.perform(post("/api/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户创建成功"));

        verify(userService).createUser(any(UserCreateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(testUserResponse);

        mockMvc.perform(put("/api/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户信息更新成功"));

        verify(userService).updateUser(eq(1L), any(UserUpdateRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户删除成功"));

        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangePassword() throws Exception {
        PasswordChangeRequest passwordRequest = new PasswordChangeRequest();
        passwordRequest.setOldPassword("oldPassword");
        passwordRequest.setNewPassword("newPassword123");
        passwordRequest.setConfirmPassword("newPassword123");

        doNothing().when(userService).changePassword(eq(1L), any(PasswordChangeRequest.class));

        mockMvc.perform(put("/api/users/1/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码修改成功"));

        verify(userService).changePassword(eq(1L), any(PasswordChangeRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testResetPassword() throws Exception {
        doNothing().when(userService).resetPassword(1L, "newPassword");

        mockMvc.perform(put("/api/users/1/reset-password")
                        .with(csrf())
                        .param("newPassword", "newPassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码重置成功"));

        verify(userService).resetPassword(1L, "newPassword");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUserStatus() throws Exception {
        doNothing().when(userService).updateUserStatus(1L, false);

        mockMvc.perform(put("/api/users/1/status")
                        .with(csrf())
                        .param("status", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户状态更新成功"));

        verify(userService).updateUserStatus(1L, false);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignRole() throws Exception {
        doNothing().when(userService).assignRole(1L, UserRole.TEACHER);

        mockMvc.perform(put("/api/users/1/role")
                        .with(csrf())
                        .param("role", "TEACHER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("角色分配成功"));

        verify(userService).assignRole(1L, UserRole.TEACHER);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserStatistics() throws Exception {
        Map<UserRole, Long> roleStats = new HashMap<>();
        roleStats.put(UserRole.ADMIN, 1L);
        roleStats.put(UserRole.TEACHER, 5L);
        roleStats.put(UserRole.STUDENT, 100L);

        UserStatisticsResponse statistics = new UserStatisticsResponse(106L, 100L, 6L, roleStats);
        when(userService.getUserStatistics()).thenReturn(statistics);

        mockMvc.perform(get("/api/users/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalUsers").value(106))
                .andExpect(jsonPath("$.data.activeUsers").value(100))
                .andExpect(jsonPath("$.data.inactiveUsers").value(6));

        verify(userService).getUserStatistics();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBatchDeleteUsers() throws Exception {
        BatchUserRequest batchRequest = new BatchUserRequest(Arrays.asList(1L, 2L, 3L));
        doNothing().when(userService).batchDeleteUsers(anyList());

        mockMvc.perform(delete("/api/users/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量删除成功"));

        verify(userService).batchDeleteUsers(anyList());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testBatchUpdateUserStatus() throws Exception {
        BatchUserRequest batchRequest = new BatchUserRequest(Arrays.asList(1L, 2L, 3L));
        doNothing().when(userService).batchUpdateUserStatus(anyList(), eq(false));

        mockMvc.perform(put("/api/users/batch/status")
                        .with(csrf())
                        .param("status", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("批量更新成功"));

        verify(userService).batchUpdateUserStatus(anyList(), eq(false));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void testGetUsersByRole() throws Exception {
        List<UserResponse> students = Arrays.asList(testUserResponse);
        when(userService.getUsersByRole(UserRole.STUDENT)).thenReturn(students);

        mockMvc.perform(get("/api/users/role/STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].username").value("testuser"));

        verify(userService).getUsersByRole(UserRole.STUDENT);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCheckUsername() throws Exception {
        when(userService.existsByUsername("testuser")).thenReturn(true);

        mockMvc.perform(get("/api/users/check-username")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).existsByUsername("testuser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCheckEmail() throws Exception {
        when(userService.existsByEmail("test@example.com")).thenReturn(true);

        mockMvc.perform(get("/api/users/check-email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        verify(userService).existsByEmail("test@example.com");
    }

    @Test
    @WithMockUser(roles = "USER")
    void testAccessDeniedForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }
}