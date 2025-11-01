package com.student.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 用户角色枚举
 */
public enum UserRole {
    ADMIN("ADMIN", "管理员"),
    TEACHER("TEACHER", "教师"),
    STUDENT("STUDENT", "学生");

    @EnumValue  // 告诉 MyBatis-Plus 使用这个字段的值与数据库进行映射
    @JsonValue  // 告诉 Jackson 序列化时使用这个字段的值
    private final String code;
    
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}