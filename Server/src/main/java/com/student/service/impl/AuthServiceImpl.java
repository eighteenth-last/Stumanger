package com.student.service.impl;

import com.student.common.exception.BusinessException;
import com.student.common.utils.JwtUtils;
import com.student.dto.LoginRequest;
import com.student.dto.LoginResponse;
import com.student.entity.User;
import com.student.mapper.UserMapper;
import com.student.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("用户登录尝试: {}", loginRequest.getUsername());
        
        // 查询用户
        User user = userMapper.findByUsernameAndStatus(loginRequest.getUsername(), true);
        if (user == null) {
            logger.warn("用户不存在或已被禁用: {}", loginRequest.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        // 验证密码
        logger.info("尝试验证密码 - 用户名: {}, 输入密码: {}, 数据库密码哈希: {}", 
                    loginRequest.getUsername(), 
                    loginRequest.getPassword(), 
                    user.getPassword());
        
        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        logger.info("密码匹配结果: {}", passwordMatches);
        
        if (!passwordMatches) {
            logger.warn("用户密码错误: {}", loginRequest.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        
        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().getCode(), user.getId());
        
        logger.info("用户登录成功: {}", loginRequest.getUsername());
        
        return new LoginResponse(token, user.getId(), user.getUsername(), 
                               user.getRealName(), user.getRole().getCode());
    }

    @Override
    public void logout(String token) {
        // 在实际应用中，可以将令牌加入黑名单
        // 这里简单记录日志
        logger.info("用户注销");
    }

    @Override
    public LoginResponse refreshToken(String token) {
        try {
            String username = jwtUtils.getUsernameFromToken(token);
            User user = userMapper.findByUsernameAndStatus(username, true);
            
            if (user == null) {
                throw new BusinessException("用户不存在或已被禁用");
            }
            
            // 生成新的令牌
            String newToken = jwtUtils.generateToken(user.getUsername(), user.getRole().getCode(), user.getId());
            
            return new LoginResponse(newToken, user.getId(), user.getUsername(), 
                                   user.getRealName(), user.getRole().getCode());
        } catch (Exception e) {
            logger.error("刷新令牌失败: {}", e.getMessage());
            throw new BusinessException("令牌刷新失败");
        }
    }
}