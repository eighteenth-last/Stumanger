package com.student.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 教师实体类
 */
@TableName("teacher")
public class Teacher {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    @NotBlank(message = "教师编号不能为空")
    @Size(max = 20, message = "教师编号长度不能超过20个字符")
    @TableField("teacher_no")
    private String teacherNo;

    @Size(max = 50, message = "所属部门长度不能超过50个字符")
    @TableField("department")
    private String department;

    @Size(max = 20, message = "职称长度不能超过20个字符")
    @TableField("title")
    private String title;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联用户信息（非数据库字段）
    @TableField(exist = false)
    private User user;

    // 构造函数
    public Teacher() {}

    public Teacher(Long userId, String teacherNo, String department, String title) {
        this.userId = userId;
        this.teacherNo = teacherNo;
        this.department = department;
        this.title = title;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", userId=" + userId +
                ", teacherNo='" + teacherNo + '\'' +
                ", department='" + department + '\'' +
                ", title='" + title + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}