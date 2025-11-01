package com.student;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码测试工具
 * 用于验证密码是否正确
 */
public class PasswordTest {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 数据库中的密码（从 SQL 文件中复制）
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHCYAKMr3Nt.jfLsKKMTIq";
        
        // 测试不同的密码
        String[] testPasswords = {"admin123", "123456", "admin", "Admin123", "ADMIN123"};
        
        System.out.println("=== 密码验证测试 ===");
        System.out.println("数据库密码: " + dbPassword);
        System.out.println();
        
        for (String password : testPasswords) {
            boolean matches = encoder.matches(password, dbPassword);
            System.out.println("测试密码: " + password + " -> " + (matches ? "✓ 匹配" : "✗ 不匹配"));
        }
        
        System.out.println();
        System.out.println("=== 生成新密码 ===");
        
        // 生成一些常用密码的 BCrypt 值
        String[] newPasswords = {"admin123", "123456", "admin"};
        for (String password : newPasswords) {
            String encoded = encoder.encode(password);
            System.out.println("明文: " + password);
            System.out.println("加密: " + encoded);
            System.out.println("验证: " + encoder.matches(password, encoded));
            System.out.println();
        }
    }
}
