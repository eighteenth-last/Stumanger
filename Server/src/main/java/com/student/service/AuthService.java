package com.student.service;

import com.student.dto.LoginRequest;
import com.student.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 用户注销
     */
    void logout(String token);
    
    /**
     * 刷新令牌
     */
    LoginResponse refreshToken(String token);
}