package com.student.controller;

import com.student.common.result.Result;
import com.student.entity.User;
import com.student.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * 临时密码重置控制器 - 仅用于开发调试
 * 生产环境请删除此文件
 */
@RestController
@RequestMapping("/api/dev")
public class PasswordResetController {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 重置管理员密码为 admin123
     * 警告：这是一个不安全的端点，仅用于开发调试
     */
    @PostMapping("/reset-admin-password")
    public Result<String> resetAdminPassword() {
        try {
            // 查找 admin 用户
            User admin = userMapper.findByUsernameAndStatus("admin", true);
            if (admin == null) {
                return Result.error("Admin user not found");
            }
            
            // 生成新密码哈希
            String newPassword = "admin123";
            String hashedPassword = passwordEncoder.encode(newPassword);
            
            logger.info("Resetting admin password");
            logger.info("New password (plain): {}", newPassword);
            logger.info("New password (hashed): {}", hashedPassword);
            
            // 更新密码
            admin.setPassword(hashedPassword);
            userMapper.updateById(admin);
            
            // 验证密码
            boolean matches = passwordEncoder.matches(newPassword, hashedPassword);
            logger.info("Password verification: {}", matches ? "SUCCESS" : "FAILED");
            
            return Result.success("Admin password reset to: admin123. Hash: " + hashedPassword);
        } catch (Exception e) {
            logger.error("Failed to reset password", e);
            return Result.error("Failed to reset password: " + e.getMessage());
        }
    }
    
    /**
     * 测试密码验证
     */
    @GetMapping("/test-password")
    public Result<String> testPassword(@RequestParam String username, 
                                       @RequestParam String password) {
        try {
            User user = userMapper.findByUsernameAndStatus(username, true);
            if (user == null) {
                return Result.error("User not found");
            }
            
            logger.info("Testing password for user: {}", username);
            logger.info("Input password: {}", password);
            logger.info("Stored hash: {}", user.getPassword());
            
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            logger.info("Password matches: {}", matches);
            
            return Result.success("Password match result: " + matches);
        } catch (Exception e) {
            logger.error("Failed to test password", e);
            return Result.error("Failed to test password: " + e.getMessage());
        }
    }
}
