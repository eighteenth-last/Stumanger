package com.student.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希生成工具
 * 用于生成BCrypt密码哈希
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成 admin123 的哈希
        String password = "admin123";
        String hash = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        System.out.println();
        
        // 验证哈希是否正确
        boolean matches = encoder.matches(password, hash);
        System.out.println("验证结果: " + matches);
        System.out.println();
        
        // 生成更多常用密码的哈希
        String[] passwords = {"admin123", "teacher123", "student123", "123456"};
        System.out.println("=== 常用密码哈希 ===");
        for (String pwd : passwords) {
            String pwdHash = encoder.encode(pwd);
            System.out.println(pwd + " -> " + pwdHash);
        }
    }
}
