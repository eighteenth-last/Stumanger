package com.student.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 性别枚举
 * 数据库存储：0-女，1-男
 * JSON 传输：FEMALE, MALE（枚举名称）
 */
public enum Gender {
    FEMALE(0, "女"),
    MALE(1, "男");

    @EnumValue  // 告诉 MyBatis-Plus 使用这个字段的值与数据库进行映射
    private final Integer code;
    
    private final String description;

    Gender(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}