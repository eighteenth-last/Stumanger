package com.student.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.common.result.Result;
import com.student.dto.*;
import com.student.entity.User;
import com.student.entity.enums.UserRole;
import com.student.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<UserResponse>> getUsers(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean status) {
        
        logger.info("分页查询用户列表 - current: {}, size: {}, username: {}, role: {}, status: {}", 
                   current, size, username, role, status);
        
        Page<User> page = new Page<>(current, size);
        IPage<UserResponse> result = userService.getUsers(page, username, role, status);
        
        return Result.success("查询成功", result);
    }

    /**
     * 获取所有用户（不分页）
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<UserResponse>> getAllUsers() {
        logger.info("获取所有用户列表");
        
        List<UserResponse> users = userService.getAllUsers();
        return Result.success("查询成功", users);
    }

    /**
     * 根据角色获取用户
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<UserResponse>> getUsersByRole(@PathVariable UserRole role) {
        logger.info("根据角色查询用户 - role: {}", role);
        
        List<UserResponse> users = userService.getUsersByRole(role);
        return Result.success("查询成功", users);
    }

    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userController.isCurrentUser(#id, authentication)")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        logger.info("获取用户详情 - id: {}", id);
        
        UserResponse user = userService.getUserById(id);
        return Result.success("查询成功", user);
    }

    /**
     * 创建新用户
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserResponse> createUser(@Validated @RequestBody UserCreateRequest request) {
        logger.info("创建新用户 - username: {}", request.getUsername());
        
        UserResponse user = userService.createUser(request);
        return Result.success("用户创建成功", user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userController.isCurrentUser(#id, authentication)")
    public Result<UserResponse> updateUser(@PathVariable Long id, 
                                         @Validated @RequestBody UserUpdateRequest request) {
        logger.info("更新用户信息 - id: {}", id);
        
        UserResponse user = userService.updateUser(id, request);
        return Result.success("用户信息更新成功", user);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        logger.info("删除用户 - id: {}", id);
        
        userService.deleteUser(id);
        return Result.success("用户删除成功", null);
    }

    /**
     * 修改用户密码
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or @userController.isCurrentUser(#id, authentication)")
    public Result<Void> changePassword(@PathVariable Long id, 
                                     @Validated @RequestBody PasswordChangeRequest request) {
        logger.info("修改用户密码 - id: {}", id);
        
        userService.changePassword(id, request);
        return Result.success("密码修改成功", null);
    }

    /**
     * 重置用户密码（管理员功能）
     */
    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> resetPassword(@PathVariable Long id, 
                                    @RequestParam String newPassword) {
        logger.info("重置用户密码 - id: {}", id);
        
        userService.resetPassword(id, newPassword);
        return Result.success("密码重置成功", null);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUserStatus(@PathVariable Long id, 
                                       @RequestParam Boolean status) {
        logger.info("更新用户状态 - id: {}, status: {}", id, status);
        
        userService.updateUserStatus(id, status);
        return Result.success("用户状态更新成功", null);
    }

    /**
     * 分配用户角色
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> assignRole(@PathVariable Long id, 
                                 @RequestParam UserRole role) {
        logger.info("分配用户角色 - id: {}, role: {}", id, role);
        
        userService.assignRole(id, role);
        return Result.success("角色分配成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public Result<UserResponse> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        logger.info("获取当前用户信息 - userId: {}", userId);
        
        if (userId == null) {
            return Result.error(401, "未授权访问");
        }
        
        UserResponse user = userService.getUserById(userId);
        return Result.success("查询成功", user);
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check-username")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        logger.debug("检查用户名是否存在 - username: {}", username);
        
        boolean exists = userService.existsByUsername(username);
        return Result.success("查询成功", exists);
    }

    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check-email")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        logger.debug("检查邮箱是否存在 - email: {}", email);
        
        boolean exists = userService.existsByEmail(email);
        return Result.success("查询成功", exists);
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserStatisticsResponse> getUserStatistics() {
        logger.info("获取用户统计信息");
        
        UserStatisticsResponse statistics = userService.getUserStatistics();
        return Result.success("查询成功", statistics);
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDeleteUsers(@Validated @RequestBody BatchUserRequest request) {
        logger.info("批量删除用户 - count: {}", request.getUserIds().size());
        
        userService.batchDeleteUsers(request.getUserIds());
        return Result.success("批量删除成功", null);
    }

    /**
     * 批量更新用户状态
     */
    @PutMapping("/batch/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchUpdateUserStatus(@Validated @RequestBody BatchUserRequest request,
                                            @RequestParam Boolean status) {
        logger.info("批量更新用户状态 - count: {}, status: {}", request.getUserIds().size(), status);
        
        userService.batchUpdateUserStatus(request.getUserIds(), status);
        return Result.success("批量更新成功", null);
    }

    /**
     * 检查是否为当前用户（用于权限控制）
     */
    public boolean isCurrentUser(Long userId, org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        try {
            String username = authentication.getName();
            UserResponse user = userService.getUserByUsername(username);
            return user.getId().equals(userId);
        } catch (Exception e) {
            logger.error("检查当前用户权限时发生错误", e);
            return false;
        }
    }
}