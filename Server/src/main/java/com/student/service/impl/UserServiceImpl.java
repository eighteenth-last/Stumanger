package com.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.dto.*;
import com.student.entity.User;
import com.student.entity.enums.UserRole;
import com.student.common.exception.BusinessException;
import com.student.mapper.UserMapper;
import com.student.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponse createUser(UserCreateRequest request) {
        logger.info("Creating user with username: {}", request.getUsername());
        
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名 '" + request.getUsername() + "' 已存在");
        }
        
        // 检查邮箱是否已存在（如果提供了邮箱）
        if (StringUtils.hasText(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱 '" + request.getEmail() + "' 已存在");
        }
        
        // 创建用户实体
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setStatus(true); // 默认启用
        
        // 保存用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("创建用户失败");
        }
        
        logger.info("User created successfully with id: {}", user.getId());
        return new UserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        logger.debug("Getting user by id: {}", id);
        
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        
        return new UserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        logger.debug("Getting user by username: {}", username);
        
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在，用户名: " + username);
        }
        
        return new UserResponse(user);
    }
    
    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        logger.info("Updating user with id: {}", id);
        
        // 获取现有用户
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        
        // 检查邮箱是否已被其他用户使用
        if (StringUtils.hasText(request.getEmail()) && !request.getEmail().equals(existingUser.getEmail())) {
            QueryWrapper<User> emailQuery = new QueryWrapper<>();
            emailQuery.eq("email", request.getEmail()).ne("id", id);
            if (userMapper.selectCount(emailQuery) > 0) {
                throw new BusinessException("邮箱 '" + request.getEmail() + "' 已被其他用户使用");
            }
        }
        
        // 更新用户信息
        if (StringUtils.hasText(request.getRealName())) {
            existingUser.setRealName(request.getRealName());
        }
        if (StringUtils.hasText(request.getEmail())) {
            existingUser.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getPhone())) {
            existingUser.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            existingUser.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            existingUser.setStatus(request.getStatus());
        }
        
        // 保存更新
        int result = userMapper.updateById(existingUser);
        if (result <= 0) {
            throw new BusinessException("更新用户失败");
        }
        
        logger.info("User updated successfully with id: {}", id);
        return new UserResponse(existingUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        
        // 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        
        // 删除用户
        int result = userMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除用户失败");
        }
        
        logger.info("User deleted successfully with id: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public IPage<UserResponse> getUsers(Page<User> page, String username, UserRole role, Boolean status) {
        logger.debug("Getting users with filters - username: {}, role: {}, status: {}", username, role, status);
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            queryWrapper.like("username", username).or().like("real_name", username);
        }
        if (role != null) {
            queryWrapper.eq("role", role);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("create_time");
        
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        IPage<UserResponse> responsePage = userPage.convert(UserResponse::new);
        
        return responsePage;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        logger.debug("Getting all users");
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        logger.debug("Getting users by role: {}", role);
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role", role).eq("status", true);
        queryWrapper.orderByDesc("create_time");
        
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public void changePassword(Long id, PasswordChangeRequest request) {
        logger.info("Changing password for user id: {}", id);
        
        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("新密码和确认密码不匹配");
        }
        
        // 获取用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("修改密码失败");
        }
        
        logger.info("Password changed successfully for user id: {}", id);
    }
    
    @Override
    public void resetPassword(Long id, String newPassword) {
        logger.info("Resetting password for user id: {}", id);
        
        // 获取用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        
        // 重置密码
        user.setPassword(passwordEncoder.encode(newPassword));
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("重置密码失败");
        }
        
        logger.info("Password reset successfully for user id: {}", id);
    }
    
    @Override
    public void updateUserStatus(Long id, Boolean status) {
        logger.info("Updating user status for id: {} to {}", id, status);
        
        // 获取用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        
        // 更新状态
        user.setStatus(status);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("更新用户状态失败");
        }
        
        logger.info("User status updated successfully for id: {}", id);
    }
    
    @Override
    public void assignRole(Long id, UserRole role) {
        logger.info("Assigning role {} to user id: {}", role, id);
        
        // 获取用户
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在，ID: " + id);
        }
        
        // 分配角色
        user.setRole(role);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException("分配角色失败");
        }
        
        logger.info("Role assigned successfully to user id: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validatePassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserStatisticsResponse getUserStatistics() {
        logger.debug("Getting user statistics");
        
        // 总用户数
        QueryWrapper<User> totalQuery = new QueryWrapper<>();
        Long totalUsers = userMapper.selectCount(totalQuery);
        
        // 活跃用户数
        QueryWrapper<User> activeQuery = new QueryWrapper<>();
        activeQuery.eq("status", true);
        Long activeUsers = userMapper.selectCount(activeQuery);
        
        // 非活跃用户数
        Long inactiveUsers = totalUsers - activeUsers;
        
        // 按角色统计
        Map<UserRole, Long> usersByRole = new HashMap<>();
        for (UserRole role : UserRole.values()) {
            QueryWrapper<User> roleQuery = new QueryWrapper<>();
            roleQuery.eq("role", role);
            Long count = userMapper.selectCount(roleQuery);
            usersByRole.put(role, count);
        }
        
        return new UserStatisticsResponse(totalUsers, activeUsers, inactiveUsers, usersByRole);
    }
    
    @Override
    public void batchDeleteUsers(List<Long> userIds) {
        logger.info("Batch deleting users - count: {}", userIds.size());
        
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException("用户ID列表不能为空");
        }
        
        // 检查所有用户是否存在
        for (Long id : userIds) {
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在，ID: " + id);
            }
        }
        
        // 批量删除
        int result = userMapper.deleteBatchIds(userIds);
        if (result != userIds.size()) {
            throw new BusinessException("批量删除用户失败");
        }
        
        logger.info("Batch deleted {} users successfully", result);
    }
    
    @Override
    public void batchUpdateUserStatus(List<Long> userIds, Boolean status) {
        logger.info("Batch updating user status - count: {}, status: {}", userIds.size(), status);
        
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException("用户ID列表不能为空");
        }
        
        // 检查所有用户是否存在并更新状态
        for (Long id : userIds) {
            User user = userMapper.selectById(id);
            if (user == null) {
                throw new BusinessException("用户不存在，ID: " + id);
            }
            user.setStatus(status);
            int result = userMapper.updateById(user);
            if (result <= 0) {
                throw new BusinessException("更新用户状态失败，ID: " + id);
            }
        }
        
        logger.info("Batch updated {} users status successfully", userIds.size());
    }
}