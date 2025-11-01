package com.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.student.dto.*;
import com.student.entity.User;
import com.student.entity.enums.UserRole;

import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 创建用户
     * @param request 用户创建请求
     * @return 用户响应
     */
    UserResponse createUser(UserCreateRequest request);
    
    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户响应
     */
    UserResponse getUserById(Long id);
    
    /**
     * 根据用户名获取用户
     * @param username 用户名
     * @return 用户响应
     */
    UserResponse getUserByUsername(String username);
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param request 用户更新请求
     * @return 用户响应
     */
    UserResponse updateUser(Long id, UserUpdateRequest request);
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void deleteUser(Long id);
    
    /**
     * 分页查询用户
     * @param page 分页参数
     * @param username 用户名（可选）
     * @param role 角色（可选）
     * @param status 状态（可选）
     * @return 分页用户列表
     */
    IPage<UserResponse> getUsers(Page<User> page, String username, UserRole role, Boolean status);
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    List<UserResponse> getAllUsers();
    
    /**
     * 根据角色获取用户
     * @param role 用户角色
     * @return 用户列表
     */
    List<UserResponse> getUsersByRole(UserRole role);
    
    /**
     * 修改用户密码
     * @param id 用户ID
     * @param request 密码修改请求
     */
    void changePassword(Long id, PasswordChangeRequest request);
    
    /**
     * 重置用户密码
     * @param id 用户ID
     * @param newPassword 新密码
     */
    void resetPassword(Long id, String newPassword);
    
    /**
     * 启用/禁用用户
     * @param id 用户ID
     * @param status 状态
     */
    void updateUserStatus(Long id, Boolean status);
    
    /**
     * 分配用户角色
     * @param id 用户ID
     * @param role 新角色
     */
    void assignRole(Long id, UserRole role);
    
    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 验证用户密码
     * @param user 用户实体
     * @param rawPassword 原始密码
     * @return 是否匹配
     */
    boolean validatePassword(User user, String rawPassword);
    
    /**
     * 获取用户统计信息
     * @return 用户统计响应
     */
    UserStatisticsResponse getUserStatistics();
    
    /**
     * 批量删除用户
     * @param userIds 用户ID列表
     */
    void batchDeleteUsers(List<Long> userIds);
    
    /**
     * 批量更新用户状态
     * @param userIds 用户ID列表
     * @param status 状态
     */
    void batchUpdateUserStatus(List<Long> userIds, Boolean status);
}